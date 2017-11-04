long randNumber;

int sensorPin = A0;
int sensorVal = 0;



void setup() {
  Serial.begin(9600);
}

void loop() {
  sensorVal = analogRead(sensorPin);
  Serial.println(String(sensorVal));
  delay(50);
}
