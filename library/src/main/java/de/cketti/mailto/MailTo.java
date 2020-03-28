/*
 * Copyright (C) 2020 cketti
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cketti.mailto;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * MailTo URL parser
 *
 * This class parses a mailto scheme URL and then can be queried for
 * the parsed parameters. This implements RFC 6068.
 *
 */
public class MailTo {
    public static final String MAILTO_SCHEME = "mailto:";

    // Well known headers
    private static final String TO = "to";
    private static final String BODY = "body";
    private static final String CC = "cc";
    private static final String BCC = "bcc";
    private static final String SUBJECT = "subject";


    /**
     * Test to see if the given string is a mailto URL
     * @param url string to be tested
     * @return true if the string is a mailto URL
     */
    public static boolean isMailTo(String url) {
        return url != null && url.startsWith(MAILTO_SCHEME);
    }

    /**
     * Parse and decode a mailto scheme string. This parser implements
     * RFC 2368. The returned object can be queried for the parsed parameters.
     * @param url String containing a mailto URL
     * @return MailTo object
     * @exception ParseException if the scheme is not a mailto URL
     */
    public static MailTo parse(String url) throws ParseException {
        if (url == null) {
            throw new NullPointerException();
        }

        if (!isMailTo(url)) {
            throw new ParseException("Not a mailto scheme");
        }

        // Drop fragment if present
        int fragmentIndex = url.indexOf('#');
        if (fragmentIndex != -1) {
            url = url.substring(0, fragmentIndex);
        }

        String address;
        String query;
        int queryIndex = url.indexOf('?');
        if (queryIndex == -1) {
            address = Uri.decode(url.substring(MAILTO_SCHEME.length()));
            query = null;
        } else {
            address = Uri.decode(url.substring(MAILTO_SCHEME.length(), queryIndex));
            query = url.substring(queryIndex + 1);
        }

        MailTo mailTo = new MailTo();

        // Parse out the query parameters
        if (query != null ) {
            String[] queries = query.split("&");
            for (String queryParameter : queries) {
                String[] nameValueArray = queryParameter.split("=", 2);
                if (nameValueArray.length == 0) {
                    continue;
                }

                // insert the headers with the name in lowercase so that
                // we can easily find common headers
                String queryParameterKey = Uri.decode(nameValueArray[0]).toLowerCase(Locale.ROOT);
                String queryParameterValue = nameValueArray.length > 1 ? Uri.decode(nameValueArray[1]) : null;

                mailTo.headers.put(queryParameterKey, queryParameterValue);
            }
        }

        // Address can be specified in both the headers and just after the
        // mailto line. Join the two together.
        String toParameter = mailTo.getTo();
        if (toParameter != null) {
            address += ", " + toParameter;
        }
        mailTo.headers.put(TO, address);

        return mailTo;
    }


    // All the parsed content is added to the headers.
    private HashMap<String, String> headers;

    /**
     * Private constructor. The only way to build a Mailto object is through
     * the parse() method.
     */
    private MailTo() {
        headers = new HashMap<>();
    }

    /**
     * Retrieve the To address line from the parsed mailto URL. This could be
     * several email address that are comma-space delimited.
     * If no To line was specified, then null is return
     * @return comma delimited email addresses or null
     */
    public String getTo() {
        return headers.get(TO);
    }

    /**
     * Retrieve the CC address line from the parsed mailto URL. This could be
     * several email address that are comma-space delimited.
     * If no CC line was specified, then null is return
     * @return comma delimited email addresses or null
     */
    public String getCc() {
        return headers.get(CC);
    }

    /**
     * Retrieve the BCC address line from the parsed mailto URL. This could be
     * several email address that are comma-space delimited.
     * If no BCC line was specified, then null is return
     * @return comma delimited email addresses or null
     */
    public String getBcc() {
        return headers.get(BCC);
    }

    /**
     * Retrieve the subject line from the parsed mailto URL.
     * If no subject line was specified, then null is return
     * @return subject or null
     */
    public String getSubject() {
        return headers.get(SUBJECT);
    }

    /**
     * Retrieve the body line from the parsed mailto URL.
     * If no body line was specified, then null is return
     * @return body or null
     */
    public String getBody() {
        return headers.get(BODY);
    }

    /**
     * Retrieve all the parsed email headers from the mailto URL
     * @return map containing all parsed values
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(MAILTO_SCHEME);
        sb.append('?');
        for (Map.Entry<String,String> header : headers.entrySet()) {
            sb.append(Uri.encode(header.getKey()));
            sb.append('=');
            sb.append(Uri.encode(header.getValue()));
            sb.append('&');
        }
        return sb.toString();
    }
}
