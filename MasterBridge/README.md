# Relution Edgerouter Server [![Build Status](http://jenkins2.office.mwaysolutions.com/buildStatus/icon?job=relution-edgerouter)](http://jenkins2.office.mwaysolutions.com/view/iot/job/relution-edgerouter/)

## Setup
Please make sure `FruityMeshConfigBuilder` was installed to your local maven repository.
See README.md of FruityMeshConfigBuilder

### Run tests

    ./bin/run_tests.sh
    
### Run the edgerouter locally via jar file for showcasing purposes
Download and unzip files

    wget http://jenkins2.office.mwaysolutions.com/view/iot/job/relution-edgerouter/lastSuccessfulBuild/artifact/meshgw.zip

Find out the usb beacon deviceid

    ls /dev/tty.usbmodem*

Edit the `config/config.json`. You need to change the http port to a port above 1024 and change the `comPort` key to the deviceid found out above
Run via

    java -jar target/*




### Remote DEBUGGING
The /etc/systemd/system/meshgateway****.service file has to be edited and the environment variable DEBUG_MESHGATEWAY=true has to be created. Afterwards, you can connect via Port 8000 (meshgateway) and Port 8001 (updateservice).