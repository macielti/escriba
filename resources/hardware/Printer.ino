void handleStyleCommand(JsonObject command) {
  const char* style = command["style"];
  // Bold
  if (strcmp(style, "b") == 0) {
    printerSerial.write(0x1B);
    printerSerial.write(0x45);
    printerSerial.write(1);
  } 
  else if (strcmp(style, "normal") == 0) {
    printerSerial.write(0x1B);
    printerSerial.write(0x45);
    printerSerial.write(0);
  }
}



void handleCommand(JsonObject command) { // Use JsonObject for items
  const char* type = command["type"];

  if (strcmp(type, "cut") == 0) {
    printerSerial.write(0x1D);
    printerSerial.write(0x56);
    printerSerial.write(0x41);
    printerSerial.write(0x03); 
  } 
  else if (strcmp(type, "print-text") == 0) {
    const char* text = command["text"].as<const char*>();
    printerSerial.println(text);
  }
  else if (strcmp(type, "style") == 0) {
    handleStyleCommand(command);
  }
  else {
    Serial.print("Unknown command: ");
    Serial.println(type);
  }
}