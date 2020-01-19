package com.overaitis.overhost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

class HTTPRequestParser {
    
    private BufferedReader br;
    private String requestMethod, fileName, queryString, formData;
    private Hashtable<String, String> headers;
    private int[] ver;

    public HTTPRequestParser(InputStream is) {
        br = new BufferedReader(new InputStreamReader(is));
        requestMethod = "";
        fileName = "";
        queryString = "";
        formData = "";
        headers = new Hashtable<String, String>();
        try {
            // Wait for HTTP request from the connection
            String line = br.readLine();

            // Bail out if line is null. In case some client tries to be 
            // funny and close immediately after connection.  (I am
            // looking at you, Chrome!)
            if (line == null) {
                return;
            }
            
            // Log client's requests.
            System.out.println("Request: " + line);

            String tokens[] = line.split(" ");

            requestMethod = tokens[0];

            if (tokens[1].indexOf("?") != -1) {
                String urlComponents[] = tokens[1].split("\\?");
                fileName = urlComponents[0];
                if (urlComponents.length > 0) {
                    queryString = urlComponents[1];
                }
            } else {
                fileName = tokens[1];
            }

            // Read and parse the rest of the HTTP headers
            int idx;
            line = br.readLine();
            while (!line.equals("")) {
                idx = line.indexOf(":");
                if (idx < 0) {
                    headers = null;
                    break;
                } else {
                    headers.put(line.substring(0, idx).toLowerCase(), 
                                line.substring(idx+1).trim());
                }
                line = br.readLine();
            }

            // read form data if POST
            if (requestMethod.equals("POST")) {
                int contentLength = getContentLength();
                final char[] data = new char[contentLength];
                for (int i = 0; i < contentLength; i++) {
                    data[i] = (char)br.read();
                }
                formData = new String(data);                
            }
        } catch (IOException e) {
            System.err.println("Unable to read/write: "  + e.getMessage());
        }
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getFileName() {
        return fileName;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getContentType() {
        return headers.get("content-type");
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get("content-length"));
    }

    public String getFormData() {
        return formData;
    }
}