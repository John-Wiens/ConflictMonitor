Master: [![Build Status](https://travis-ci.org/usdot-jpo-ode/jpo-cvdp.svg?branch=master)](https://travis-ci.org/usdot-jpo-ode/jpo-cvdp) [![Quality Gate](https://sonarqube.com/api/badges/gate?key=jpo-cvdp-key)](https://sonarqube.com/dashboard?id=jpo-cvdp-key)

# jpo-cvdp

The United States Department of Transportation Joint Program Office (JPO)
Connected Vehicle Data Privacy (CVDP) Project is developing a variety of methods
to enhance the privacy of individuals who generated connected vehicle data.

Connected vehicle technology uses in-vehicle wireless transceivers to broadcast
and receive basic safety messages (BSMs) that include accurate spatiotemporal
information to enhance transportation safety. Integrated Global Positioning
System (GPS) measurements are included in BSMs.  Databases, some publicly
available, of BSM sequences, called trajectories, are being used to develop
safety and traffic management applications. **BSMs do not contain explicit
identifiers that link trajectories to individuals; however, the locations they
expose may be sensitive and associated with a very small subset of the
population; protecting these locations from unwanted disclosure is extremely
important.** Developing procedures that minimize the risk of associating
trajectories with individuals is the objective of this project.

# The Operational Data Environment (ODE) Privacy Protection Module (PPM)

The PPM operates on streams of raw BSMs generated by the ODE. It determines
whether individual BSMs should be retained or suppressed (deleted) based on the
information in that BSM and auxiliary map information used to define a geofence.
BSM geoposition (latitude and longitude) and speed are used to determine the
disposition of each BSM processed. The PPM also redacts other BSM fields.

## PPM Limitations

Protecting against inference-based privacy attacks on spatiotemporal
trajectories (i.e., sequences of BSMs from a single vehicle) in **general** is
a challenging task. An example of an inference-based privacy attack is
identifying the driver that generated a sequence of BSMs using specific
locations they visit during their trip, or other features discernable from the
information in the BSM sequence. **This PPM treats a specific use case: a
geofenced area where residences do not exist, e.g., a highway corridor, with
certain speed restrictions.** Do not assume this strategy will work in general.
There are alternative strategies that must be employed to handle cases where
loitering locations can aid in learning the identity of the driver.

## Table of Contents

1. [Release Notes](#release-notes)
2. [Documentation](#documentation)
3. [Development and Collaboration Tools](#development-and-collaboration-tools)
3. [Getting Started](#getting-started)
4. [Installation](docs/installation.md)
5. [Configuration and Operation](docs/configuration.md)
6. [Testing](docs/testing.md)
7. [Development](docs/coding-standards.md)

## Release Notes

### ODE Sprint 38

- ODE-771: Fixed reported bug where the PPM exits when connections to Kafka brokers fail.

### ODE Sprint 15

- ODE-369/ORNL-15: Logging
- Updated Identifier Redactor to include random assignment in lieu of fixed assignment.

### ODE Sprint 14

- ORNL-17: USDOT Playbook

### ODE Sprint 13

- ODE-290: Integration with the ODE.

### ODE Sprint 12

- ODE-77: Complete documentation

### ODE Sprint 11

- (Partial Complete) ODE-282 Implement a Module that Interfaces with the ODE.
- (Partially Complete) ODE-77 Implement a PPM that uses a Geofence to Filter BSMs.

# Documentation

The following document will help practitioners build, test, deploy, and understand the PPM's functions:

- [Privacy Protection Module User Guide](docs/ppm_user_manual.docx)

All stakeholders are invited to provide input to these documents. Stakeholders should direct all input on this document
to the JPO Product Owner at DOT, FHWA, or JPO. To provide feedback, we recommend that you create an "issue" in this
repository (https://github.com/usdot-jpo-ode/jpo-cvdp/issues). You will need a GitHub account to create an issue. If you
don’t have an account, a dialog will be presented to you to create one at no cost.

## Code Documentation

Code documentation can be generated using [Doxygen](https://www.doxygen.org) by following the commands below:

```bash
$ sudo apt install doxygen
$ cd <install root>/jpo-cvdp
$ doxygen
```

The documentation is in HTML and is written to the `<install root>/jpo-cvdp/docs/html` directory. Open `index.html` in a
browser.

# Development and Collaboration Tools

## Source Repositories - GitHub

- https://github.com/usdot-jpo-ode/jpo-cvdp
- `git@github.com:usdot-jpo-ode/jpo-cvdp.git`

## Agile Project Management - Jira
https://usdotjpoode.atlassian.net/secure/Dashboard.jspa

## Continuous Integration and Delivery

The PPM is tested using [Travis Continuous Integration](https://travis-ci.org).

# Getting Started

## Prerequisites

You will need Git to obtain the code and documents in this repository.
Furthermore, we recommend using Docker to build the necessary containers to
build, test, and experiment with the PPM. The [Docker](#docker) instructions can be found in that section.

- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com)

You can find more information in our [installation and setup](docs/installation.md) directions.

## Getting the Source Code

See the installation and setup instructions unless you just want to examine the code.

**Step 1.** Disable Git `core.autocrlf` (Only the First Time)

   **NOTE**: If running on Windows, please make sure that your global git config is
   set up to not convert End-of-Line characters during checkout. This is important
   for building docker images correctly.

```bash
git config --global core.autocrlf false
```

**Step 2.** Clone the source code from GitHub repositories using Git commands:

```bash
git clone https://github.com/usdot-jpo-ode/jpo-cvdp.git
```