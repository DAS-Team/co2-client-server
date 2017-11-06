#include <MQ135.h>

long randNumber;

int sensor1Pin = A0;
int sensor1Val = 0;
//int sensor2Pin = A1;
//int sensor2Val = 0;

MQ135 mqSensor = MQ135(sensor1Pin, MQ135_RLOAD, 288.0);



void setup() {
  Serial.begin(9600);
  //mqSensor = MQ135(sensor1Pin);
}

void loop() {
  sensor1Val = analogRead(sensor1Pin);
  //sensor2Val = analogRead(sensor2Pin);
  
  //Serial.println(String((sensor1Val + sensor2Val) / 2));
  //Serial.println(mqSensor.getPPM());
  //Serial.println(mqSensor.getResistance());
  //Serial.println(sensor1Val / 1024.0 * 5);
  Serial.println(mqSensor.getPPM());
  
  delay(50);
}

