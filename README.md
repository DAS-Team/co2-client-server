# co2-client-server

## Before any setup

Extract these files from the archive

## Quick run on Linux (no sensors or Raspberries needed)

1. Run `./run_server.sh`
2. Run `./run_client_floor1.sh`
3. Run `./run_client_floor2.sh` (each in different terminal)

## Raspberry Pi Setup

1. Download the latest Raspbian Stretch Lite from [here](https://www.raspberrypi.org/downloads/raspbian/).
2. Verify SHA-256.
3. Extract.
4. Flash to SD card, following instructions [here](https://www.raspberrypi.org/documentation/installation/installing-images/README.md).
5. Place a file called `ssh` with no file extension on the boot partition of the flashed SD card. This will enable ssh on the Pi.
6. Connect Pi to the network with an ethernet cable.
7. ssh to the Pi with `ssh pi@<ip_addr>`. Password is `raspberry`.
8. Run `sudo apt update` then `sudo apt upgrade`. This might take a while.
9. Run `sudo apt install git oracle-java8-jdk librxtx-java`
10. Run `curl -s get.pi4j.com | sudo bash`
11. Enable SPI connection by running `sudo raspi-config`, then going to `Interfacing Options > SPI`
12. Reboot with `sudo reboot`.
13. Unplug the Pi then wire it up as shown in the `Hardware SPI` instructions [here](https://learn.adafruit.com/raspberry-pi-analog-to-digital-converters/mcp3008). Plug back in.
14. Run `mkdir co2`, then `cd co2/`.
15. Run `git clone https://github.com/DAS-Team/co2-client-server.git`, then `cd co2-client-server`.
16. Run `./gradlew build`.
17. Run `./gradlew runClient -Djava.rmi.server.hostname=<client_ip> -Dexec.args="<server_ip> <floor_number> <rzero_val>"`

## Test Client Setup

1. Run `sudo apt update` then `sudo apt upgrade`.
2. Run `sudo apt install git oracle-java8-jdk librxtx-java`
3. Run `curl -s get.pi4j.com | sudo bash`
4. Run `mkdir co2`, then `cd co2/`
5. Run `git clone https://github.com/DAS-Team/co2-client-server.git`, then `cd co2-client-server`.
6. Run `./gradlew build`.
7. Run `./gradlew runTestClient -Djava.rmi.server.hostname=<client_ip> -Dexec.args="<server_ip> <floor_number>"`

## Stress Test Setup

1. Run `sudo apt update` then `sudo apt upgrade`.
2. Run `sudo apt install git oracle-java8-jdk librxtx-java`
3. Run `curl -s get.pi4j.com | sudo bash`
4. Run `mkdir co2`, then `cd co2/`
5. Run `git clone https://github.com/DAS-Team/co2-client-server.git`, then `cd co2-client-server`.
6. Run `./gradlew build`.
7. Run `./gradlew runStressTestClients -Djava.rmi.server.hostname=<client_ip> -Dexec.args="<server_ip> <seconds_to_run_for> <num_clients>"`

## Server Setup

1. Run `sudo apt update` then `sudo apt upgrade`.
2. Run `sudo apt install git oracle-java8-jdk librxtx-java`
3. Run `curl -s get.pi4j.com | sudo bash`
4. Run `mkdir co2`, then `cd co2/`
5. Run `git clone https://github.com/DAS-Team/co2-client-server.git`, then `cd co2-client-server`.
6. Run `./gradlew build`.
7. Run `./gradlew runServer -Djava.rmi.server.hostname=<server_ip> -Dexec.args="<server_ip>"`
