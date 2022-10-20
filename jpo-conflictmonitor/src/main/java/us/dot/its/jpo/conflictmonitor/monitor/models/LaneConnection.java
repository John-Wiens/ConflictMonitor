package us.dot.its.jpo.conflictmonitor.monitor.models;

import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;

import us.dot.its.jpo.ode.plugin.j2735.J2735GenericLane;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeOffsetPointXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeSetXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735Node_XY;

public class LaneConnection{

    private J2735GenericLane ingress;
    private J2735GenericLane egress;
    private LineString connectingLine;
    private GeometryFactory geometryFactory;
    

    /**
     * @param ingress
     * @param egress
     */
    public LaneConnection(J2735GenericLane ingress, J2735GenericLane egress) {
        System.out.println("Created new Lane Connection");
        this.ingress = ingress;
        this.egress = egress;
        this.geometryFactory = JTSFactoryFinder.getGeometryFactory();

        this.connectingLine = findConnectionLine();

        
    }

    public LineString findConnectionLine(){

        //System.out.println("Lane" + ingress);
		//System.out.println("Connecting Lane" + egress);

        LineString ingressString = laneToLineString(ingress);
        LineString egressString = laneToLineString(egress);
        LineString connectionString = getConnectingLineString(ingressString, egressString);

        printLineString(ingressString);
        printLineString(egressString);
        printLineString(connectionString);

        return connectionString;

    }

    public LineString getConnectingLineString(LineString startSegment, LineString endSegment){
        Coordinate[] startCoordinates = startSegment.getCoordinates();
        Coordinate[] endCoordinates = endSegment.getCoordinates();

        Coordinate[] connection = {
            startCoordinates[0],
            endCoordinates[0]
        };

        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(connection);
        LineString connectionString = new LineString(sequence, geometryFactory);
        return connectionString;

    }


    public LineString laneToLineString(J2735GenericLane lane){
        List<J2735NodeXY> nodes = lane.getNodeList().getNodes().getNodes();

        Coordinate[] coordList = new Coordinate[nodes.size()]; 
        for(int i=0; i<nodes.size(); i++){
            if(nodes.get(i).getDelta().getNodeLatLon() != null){
                System.out.println("Reference Point Moved");
            }
            J2735NodeOffsetPointXY nodeOffset = nodes.get(i).getDelta();
            J2735Node_XY nodeXY = getNodeXY(nodeOffset);

            double offsetX = nodeXY.getX().doubleValue();
            double offsetY = nodeXY.getY().doubleValue();

            

            if (i > 1){
                coordList[i] = new Coordinate(coordList[i-1].x + offsetX, coordList[i-1].y + offsetY);
            } else{
                coordList[i] = new Coordinate(offsetX, offsetY);
            }
        }

        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(coordList);
        return new LineString(sequence, geometryFactory);
    }

    public J2735Node_XY getNodeXY(J2735NodeOffsetPointXY nodeOffset){
        if (nodeOffset.getNodeXY1() != null){
            return nodeOffset.getNodeXY1();
        }
        else if (nodeOffset.getNodeXY2() != null){
            return nodeOffset.getNodeXY2();
        }  
        else if (nodeOffset.getNodeXY3() != null){
            return nodeOffset.getNodeXY3();
        }  
        else if (nodeOffset.getNodeXY4() != null){
            return nodeOffset.getNodeXY4();
        }  
        else if (nodeOffset.getNodeXY5() != null){
            return nodeOffset.getNodeXY5();
        }   
        else if (nodeOffset.getNodeXY6() != null){
            return nodeOffset.getNodeXY6();
        }
        else{
            return null;
        }
    }

    public J2735GenericLane getIngress() {
        return ingress;
    }

    public void setIngress(J2735GenericLane ingress) {
        this.ingress = ingress;
    }

    public J2735GenericLane getEgress() {
        return egress;
    }

    public void setEgress(J2735GenericLane egress) {
        this.egress = egress;
    }

    public void printLineString(LineString lstring){
        System.out.println("Line String");
        Coordinate[] coords = lstring.getCoordinates();

        for(int i=0; i< coords.length; i++){
            System.out.println(coords[i].x + ","+ coords[i].y);
        }
    }

}