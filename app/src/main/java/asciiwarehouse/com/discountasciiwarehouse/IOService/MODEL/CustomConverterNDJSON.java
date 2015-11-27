package asciiwarehouse.com.discountasciiwarehouse.IOService.MODEL;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by mirkomesner on 11/23/15.
 */

///we convert NDJSON :)

public class CustomConverterNDJSON implements Converter {

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        List<WarehouseItemModel> objects = null;
        try {
            objects = fromStream(body.in());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objects;

    }

    @Override
    public TypedOutput toBody(Object object) {
        return null;
    }

    // Custom method to convert stream from request to string
    public static List<WarehouseItemModel> fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        out.append("[");
        String newLine = ",";

        for (String next, line = reader.readLine(); line != null; line = next) {
            out.append(line);
            next = reader.readLine();
            if(next!=null)
                out.append(newLine);
        }

        out.append("]");

        Type listType = new TypeToken<List<WarehouseItemModel>>() {
        }.getType();

        Gson gson = new Gson();
        List<WarehouseItemModel> arr = gson.fromJson(out.toString(), listType);

        return arr;
    }
}