#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <DNSServer.h>
#include <WiFiManager.h>   // https://github.com/tzapu/WiFiManager
#include <LittleFS.h>      // File System
#include <ArduinoJson.h>   // Install "ArduinoJson" by Benoit Blanchon

#include <SoftwareSerial.h>

SoftwareSerial printerSerial(13, 15); // RX, TX (GPIO 13 e 15)

bool shouldSaveConfig = false;

void saveConfigCallback () {
  Serial.println("Should save config");
  shouldSaveConfig = true;
}

const int TRIGGER_PIN = 0;

void setup() {

  Serial.begin(115200);
  
  // Comunication with printer
  printerSerial.begin(38400);

  pinMode(TRIGGER_PIN, INPUT_PULLUP);

  WiFiManager wifiManager;

  wifiManager.setTitle("escriba.brunolab.dev.br - IOT: Configure a sua unidade");
  wifiManager.setClass("invert");

  wifiManager.setSaveConfigCallback(saveConfigCallback);

  if (!wifiManager.autoConnect("escriba.brunolab.dev.br - IOT", "password")) {
    Serial.println("failed to connect and hit timeout");
    delay(3000);
    ESP.restart();
  }

  Serial.println("\nConnected to WiFi!");  
}

void loop() {

  delay(2000);

  JsonDocument documetResp; 

  if (fetchJsonData(documetResp)) {
    JsonDocument document = documetResp["documents"][0];
    JsonArray commands = document["commands"];

    printerSerial.write(0x1B);
    printerSerial.write(0x40);

    for (JsonObject command : commands) {
      if (command) {
        const char* type = command["type"];
        const char* id = command["id"];

        Serial.print("Found Document ID: ");
        Serial.print(id);
        Serial.print("\nCommand Type: ");
        Serial.println(type ? type : "N/A");
        handleCommand(command);
      }
    }
    if (!document.isNull()) {
      ackDocument(document);
    }
  }

  if ( digitalRead(TRIGGER_PIN) == LOW ) {

    Serial.println("Button pressed... waiting for long press...");

    delay(3000);

    if ( digitalRead(TRIGGER_PIN) == LOW ) {
      Serial.println("Resetting WiFi Credentials...");

      WiFiManager wifiManager;
      wifiManager.resetSettings();

      pinMode(LED_BUILTIN, OUTPUT);
      for(int i=0; i<5; i++){
        digitalWrite(LED_BUILTIN, LOW); delay(100);
        digitalWrite(LED_BUILTIN, HIGH); delay(100);
      }

      ESP.restart();
    }
  }
}