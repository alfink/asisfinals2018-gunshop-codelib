/**
 * The ARTist Project (https://artist.cispa.saarland)
 *
 * Copyright (C) 2017 CISPA (https://cispa.saarland), Saarland University
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
 *
 * @author "Oliver Schranz <oliver.schranz@cispa.saarland>"
 * @author "Sebastian Weisgerber <weisgerber@cispa.saarland>"
 *
 */
package saarland.cispa.artist.codelib;

import android.util.Log;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CodeLib {

    @interface Inject {}

    // Instance variable for singleton usage ///////////////////////////////////////////////////////
    public static CodeLib INSTANCE = new CodeLib();

    // <Constants> /////////////////////////////////////////////////////////////////////////////////
    private static final String TAG = "GunshopCodeLib";
    private static final String VERSION = TAG + " # 1.0.0";

    // </Constants> ////////////////////////////////////////////////////////////////////////////////

    /**
     * Static Class Constructor
     */
    static {
        // <Code>
    }

    /**
     * Private Class Constructor
     * => Forbidden Class Initialisation (Singleton)
      */
    private CodeLib() {
        Log.v(TAG, TAG + " CodeLib() " + VERSION);
    }


    @SuppressWarnings("unused")
    public void init(Object obj) {
    }

    @SuppressWarnings("unused")
    public void okhttp2interceptor(Object obj) {
        //TODO
    }

    private static Proxy proxy;

    private static Thread resolveProxy =
        new Thread() {
            @Override
            public void run() {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy", 8080));
            }
        };

    private Proxy getProxy(){
        while (proxy == null) {
            resolveProxy.start();
            try {
                resolveProxy.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return proxy;
    }

    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
    };

    @Inject
    @SuppressWarnings("unused")
    public URLConnection openConnection(URL obj){
        try {
            Log.i("ASIS-codelib", "opening connection to:" + obj.toString());
            URLConnection c = obj.openConnection(getProxy());
            if (c instanceof HttpsURLConnection){
                try {
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                    ((HttpsURLConnection) c).setSSLSocketFactory(sc.getSocketFactory());
                } catch (Exception e) {
                }
            }
            return c;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Inject
    public void log(String s){
        Log.w("ASIS-codelib-log", s);
    }
}
