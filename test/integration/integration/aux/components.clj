(ns integration.aux.components
  (:require [clojure.test :refer :all]))

(def schemas ["CREATE TABLE IF NOT EXISTS documents (id UUID PRIMARY KEY, status VARCHAR(32) NOT NULL, created_at TIMESTAMP NOT NULL, retrieved_at TIMESTAMP, completed_at TIMESTAMP, failed_at TIMESTAMP);"
              "CREATE TABLE IF NOT EXISTS commands (id UUID PRIMARY KEY, index INTEGER NOT NULL, document_id UUID NOT NULL, type VARCHAR(50) NOT NULL, scan_lines INTEGER);"])
