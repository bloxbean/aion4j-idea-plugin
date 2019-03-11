import org.aion.avm.core.util.AvmDetails;

import java.util.List;
import java.util.Map;
import com.eclipsesource.json.*;

//This class is invoked by IDE with project's JDK to get JCLWhitelist
public class AvmDetailsGetter {

    public static void main(String[] args) throws ClassNotFoundException {

        Map<Class<?>, List<AvmDetails.MethodDescriptor>>  details = AvmDetails.getClassLibraryWhiteList();

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

        System.out.println(detailsObj);
    }
}
