long randNumber;

int sensor1Pin = A0;
int sensor1Val = 0;
int sensor2Pin = A1;
int sensor2Val = 0;



void setup() {
  Serial.begin(9600);
}

void loop() {
  sensor1Val = analogRead(sensor1Pin);
  sensor2Val = analogRead(sensor2Pin);
  
  Serial.println(String((sensor1Val + sensor2Val) / 2));
  delay(50);
}
