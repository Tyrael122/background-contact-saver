package br.makesoftware.contacts_manager.adapters;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.makesoftware.contacts_manager.constants.EndpointsConstants;
import br.makesoftware.contacts_manager.interfaces.ApiAdapter;
import br.makesoftware.contacts_manager.utils.FileLogger;
import br.makesoftware.contacts_manager.constants.LogType;

public class XmlApiAdapter implements ApiAdapter {
    private FileLogger fileLogger;
    public XmlApiAdapter(FileLogger fileLogger) {
        this.fileLogger = fileLogger;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public List<String> requestContactsNotSent() throws Exception {
        HttpURLConnection urlConnection = connectToEndpoint();
        
        List<String> phoneNumbers;
        
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            
            phoneNumbers = parsePhoneNumbersFromJsonResponse(in);
        } catch (JSONException e) {
            fileLogger.logError("Ocorreu um erro ao obter o corpo da resposta http da API: '" + e.getMessage() + "'", LogType.STATUS);
            throw new RuntimeException(e);
        } finally {
            urlConnection.disconnect();
        }
        
        return phoneNumbers;
    }

    private HttpURLConnection connectToEndpoint() {
        URL url;
        HttpURLConnection urlConnection;

        try {
            url = new URL(EndpointsConstants.RETRIEVE_CONTACTS_TO_SAVE_ENDPOINT);
            urlConnection = (HttpURLConnection) url.openConnection();

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return urlConnection;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private List<String> parsePhoneNumbersFromJsonResponse(InputStream in) throws JSONException {
        String jsonString;
        try {
            StringBuilder jsonStringBuilder = parseStringFromInputStream(in);
            jsonString = sanitizeJsonStringBuilder(jsonStringBuilder);
        } catch (IOException e) {
            fileLogger.logError("Ocorreu um erro ao concatenar em uma string o JSON retornado pela API:" + e.getMessage(), LogType.STATUS);
            throw new RuntimeException(e);
        }

        List<String> telefones = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString.toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String telefone = jsonObject.getString("Telefone");
            telefones.add(telefone);
        }

        return telefones;
    }

    @NonNull
    private static StringBuilder parseStringFromInputStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder parsedString = new StringBuilder();
        
        String line;
        while ((line = reader.readLine()) != null) {
            parsedString.append(line);
        }
        
        return parsedString;
    }

    private String sanitizeJsonStringBuilder(StringBuilder jsonStringBuilder) {
        String jsonString = jsonStringBuilder.toString().replace("\\r", "").replace("\\n", "");

        jsonString = "[" + jsonString.substring(2, jsonString.length() - 2) + "]";

        return jsonString
                .replace("\\", "");
    }
}
