import org.aion.avm.core.util.AvmDetails;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import com.eclipsesource.json.*;

//This class is invoked by IDE with project's JDK to get JCLWhitelist
public class AvmDetailsGetter {

    public static void main(String[] args) throws ClassNotFoundException, IOException {

        JsonObject detailsObj = getJCLWhitelist();
        writeOutput(args[0], detailsObj);
    }

    public static JsonObject getJCLWhitelist() throws ClassNotFoundException {
        Map<Class<?>, List<AvmDetails.MethodDescriptor>> details = AvmDetails.getClassLibraryWhiteList();

        JsonObject detailsObj = Json.object();
        details.entrySet().stream().forEach(e -> {

            JsonObject classJsonObj = Json.object();

            List<AvmDetails.MethodDescriptor> methods = e.getValue();

            JsonArray methodArray = Json.array();
            for(AvmDetails.MethodDescriptor methodDescriptor: methods) {
                JsonObject methodObj = Json.object();
                methodObj.add("name", methodDescriptor.name);
                methodObj.add("isStatic", methodDescriptor.isStatic);

                JsonArray paramArrays = Json.array();
                for(Class arg: methodDescriptor.parameters) {
                    paramArrays.add(arg.getCanonicalName());
                }

                methodObj.add("parameters", paramArrays);
                methodArray.add(methodObj);
            }

            detailsObj.add(e.getKey().getCanonicalName(), methodArray);
        });

        return detailsObj;
    }

    public static void writeOutput(String fileName, JsonObject jsonObject) throws IOException {

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            jsonObject.writeTo(fileWriter);
        } catch (Exception e) {
            throw e;
        }
    }
}
