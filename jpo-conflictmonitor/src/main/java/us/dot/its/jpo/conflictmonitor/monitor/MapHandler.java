package us.dot.its.jpo.conflictmonitor.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.math.BigDecimal;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.util.URLs;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.models.LaneConnection;
import us.dot.its.jpo.ode.model.OdeMapData;
import us.dot.its.jpo.ode.model.OdeMapMetadata;
import us.dot.its.jpo.ode.model.OdeMapPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionGeometry;
import us.dot.its.jpo.ode.plugin.j2735.J2735MAP;
import us.dot.its.jpo.ode.plugin.j2735.J2735Connection;
import us.dot.its.jpo.ode.plugin.j2735.J2735ConnectsToList;
import us.dot.its.jpo.ode.plugin.j2735.J2735GenericLane;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
import us.dot.its.jpo.ode.wrapper.MessageProducer;



public class MapHandler extends AbstractSubscriberProcessor<String, String> {
  private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ConflictMonitorProperties geojsonProperties;
	private MessageProducer<String, String> geoJsonProducer;
	protected final ObjectMapper mapper = new ObjectMapper();

	public MapHandler(ConflictMonitorProperties geojsonProps) {
		super();
		this.geojsonProperties = geojsonProps;
		this.geoJsonProducer = MessageProducer.defaultStringMessageProducer(geojsonProperties.getKafkaBrokers(),
			geojsonProperties.getKafkaProducerType(), geojsonProperties.getKafkaTopicsDisabledSet());
	}

	@Override
	public Object process(String consumedData) {
		try {

			System.out.println("Received New Map Message");
			
			OdeMapData map = deserializeMap(consumedData);
			extractConnections(map);

			
		} catch (Exception e) {
			logger.error("Failed to convert received data to GeoJSON: " + consumedData, e);
		}
		return null;
  	}


	public void extractConnections(OdeMapData map){
		OdeMapPayload payload = (OdeMapPayload)map.getPayload();
		J2735MAP payloadMap = payload.getMap();
		List<J2735IntersectionGeometry> intersections = payloadMap.getIntersections().getIntersections();

		for(int i =0; i < intersections.size(); i++){
			J2735IntersectionGeometry intersection = intersections.get(i);
			List<J2735GenericLane> lanes = intersection.getLaneSet().getLaneSet();

			ArrayList<LaneConnection> laneConnections = new ArrayList<>();
			
			for(int j=0; j< lanes.size(); j++){
				J2735GenericLane lane = lanes.get(j);
				J2735ConnectsToList connectsTo = lane.getConnectsTo();
				if(connectsTo != null){
					List<J2735Connection> connection = connectsTo.getConnectsTo();
					for(int k =0; k < connection.size(); k++){
						int connectingLane = connection.get(k).getConnectingLane().getLane();
						
						
						
						laneConnections.add(new LaneConnection(lane, lanes.get(connectingLane-1)));
						System.out.println(lane.getManeuvers());
						break;
					}
					break;
				}
			}

			System.out.println(laneConnections.size());

			break; // Only run for one intersection for now
		}
		
		
	}

	public OdeMapData deserializeMap(String mapString){
		if (mapString == null) {
			return null;
		}
		//try {
		JsonNode actualObj;
		try {
			actualObj = mapper.readTree(mapString);
			JsonNode metadataNode = actualObj.get("metadata");
			String metadataString = metadataNode.toString();
			OdeMapMetadata metadataObject = (OdeMapMetadata) JsonUtils.fromJson(metadataString, OdeMapMetadata.class);
			// Deserialize the payload
			JsonNode payloadNode = actualObj.get("payload");
			String payloadString = payloadNode.toString();
			OdeMapPayload mapPayload = (OdeMapPayload) JsonUtils.fromJson(payloadString, OdeMapPayload.class);
			OdeMapData returnData = new OdeMapData(metadataObject, mapPayload);
			return returnData;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}