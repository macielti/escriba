#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h> // Required for HTTPS
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

bool fetchJsonData(JsonDocument& doc) {
  WiFiClientSecure client;
  HTTPClient http;
  bool success = false;

  client.setInsecure();

  const char* url = "https://api-escriba.brunolab.dev.br/api/documents";

  Serial.print("[HTTPS] Connecting to: ");
  Serial.println(url);

  if (http.begin(client, url)) {
    int httpCode = http.GET();

    if (httpCode == HTTP_CODE_OK) {
      String payload = http.getString();
      DeserializationError error = deserializeJson(doc, payload);

      if (!error) {
        success = true;
      } else {
        Serial.print(F("JSON Error: "));
        Serial.println(error.f_str());
      }
    } else {
      Serial.printf("[HTTPS] GET failed, error: %s\n", http.errorToString(httpCode).c_str());
    }
    http.end();
  }
  return success;
}

bool ackDocument(JsonDocument document) {
  WiFiClientSecure client;
  HTTPClient http;
  bool success = false;

  client.setInsecure();

  String url = "https://api-escriba.brunolab.dev.br/api/documents/";
  url += document["id"].as<const char*>();
  url += "/ack";

  Serial.print("[HTTPS] PUT to: ");
  Serial.println(url);

  if (http.begin(client, url)) {

    int httpCode = http.sendRequest("PUT", ""); 

    if (httpCode == HTTP_CODE_OK || httpCode == 204) {
      success = true;
      Serial.println("[HTTPS] Acknowledged successfully");
    } else {
      Serial.printf("[HTTPS] PUT failed, error: %s (Code: %d)\n", 
                    http.errorToString(httpCode).c_str(), httpCode);
    }
    http.end();
  }
  return success;
}