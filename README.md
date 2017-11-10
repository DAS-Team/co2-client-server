# co2-client-server

### Dependencies

Depends on both the native and Java components of `jrxtx`.

The native parts can be installed on a Debian based system with:

`sudo apt-get install librxtx-java`

The Java API can be installed using Gradle.

## Raspberry Pi Setup

1. Download the latest Raspbian Stretch Lite from [here](https://www.raspberrypi.org/downloads/raspbian/).
2. Verify SHA-256.
3. Extract.
4. Flash to SD card, following instructions [here](https://www.raspberrypi.org/documentation/installation/installing-images/README.md).
5. Place a file called `ssh` with no file extension on the boot partition of the flashed SD card. This will enable ssh on the Pi.
6. Connect Pi to the network with an ethernet cable.
7. ssh to the Pi with `ssh pi@<ip_addr>`. Password is `raspberry`.
8. Run `sudo apt update` then `sudo apt upgrade`. This might take a while so go make dinner or something.
9. Run `sudo apt install git oracle-java8-jdk librxtx-java`
10. Run `curl -s get.pi4j.com | sudo bash`
11. Enable SPI connection by running `sudo raspi-config`, then going to `Interfacing Options > SPI`
11. Reboot with `sudo reboot`.
11. Unplug the Pi then wire it up as shown in the `Hardware SPI` instructions [here](https://learn.adafruit.com/raspberry-pi-analog-to-digital-converters/mcp3008). Plug back in.
11. Run `mkdir co2`, then `cd co2/`.
12. Run `git clone https://github.com/DAS-Team/co2-client-server.git`, then `cd co2-client-server`.
13. Run `./gradlew build`.
14. Run `./gradlew runClient -Djava.rmi.server.hostname="<client_ip>" -Dexec.args="<server_ip> <floor_number>"`

## Server Setup

8. Run `sudo apt update` then `sudo apt upgrade`. This might take a while so go make dinner or something.
9. Run `sudo apt install git oracle-java8-jdk librxtx-java`
10. Run `curl -s get.pi4j.com | sudo bash`
11. Run `mkdir co2`, then `cd co2/`
12. Run `git clone https://github.com/DAS-Team/co2-client-server.git`, then `cd co2-client-server`.
13. Run `./gradlew build`.
14. Run `./gradlew runServer -Djava.rmi.server.hostname="<server_ip>" -Dexec.args="<server_ip>"`
