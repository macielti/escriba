void handleSizeCommand(JsonObject command) {
  const char* size = command["normal"];
  if (strcmp(style, "bold") == 0) {
    printerSerial.write(0x1B);
    printerSerial.write(0x45);
    printerSerial.write(1);
  }
}

void handleStyleCommand(JsonObject command) {
  const char* style = command["style"];
  if (strcmp(style, "bold") == 0) {
    printerSerial.write(0x1B);
    printerSerial.write(0x45);
    printerSerial.write(1);
  }
  else if (strcmp(style, "underline") == 0) {
    printerSerial.write(0x1B);
    printerSerial.write(0x2D);
    printerSerial.write(1);
  }
  else if (strcmp(style, "italic") == 0) {
    printerSerial.write(0x1B);
    printerSerial.write(0x34);
    printerSerial.write(1);
  }
  else if (strcmp(style, "normal") == 0) {
    // disable bold
    printerSerial.write(0x1B);
    printerSerial.write(0x45);
    printerSerial.write(0);
    // disable underline 
    printerSerial.write(0x1B);
    printerSerial.write(0x2D);
    printerSerial.write(0);
    // disable italic
    printerSerial.write(0x1B);
    printerSerial.write(0x34);
    printerSerial.write(0);
  }
}

void handleAlignCommand(JsonObject command) {
  const char* orientation = command["orientation"];
  if (strcmp(orientation, "lt") == 0) {
    printerSerial.write(0x1B);
    printerSerial.write(0x61);
    printerSerial.write(0);
  } 
  else if (strcmp(orientation, "ct") == 0) {
    printerSerial.write(0x1B);
    printerSerial.write(0x61);
    printerSerial.write(1);
  }
  else if (strcmp(orientation, "rt") == 0) {
    printerSerial.write(0x1B);
    printerSerial.write(0x61);
    printerSerial.write(2);
  }
}

void handleCommand(JsonObject command) {
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
  else if (strcmp(type, "feed-paper") == 0) {
    printerSerial.write(0x1B);
    printerSerial.write(0x64);
    int lines = command["lines"] | 1;
    printerSerial.write((uint8_t)lines);  
  }
  else if (strcmp(type, "align") == 0) {
    handleAlignCommand(command);
  }
  else {
    Serial.print("Unknown command: ");
    Serial.println(type);
  }
}