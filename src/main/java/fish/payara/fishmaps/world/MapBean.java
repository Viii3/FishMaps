package fish.payara.fishmaps.world;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.util.List;

@Named(value = "MapBean")
@RequestScoped
public class MapBean {
    @EJB
    private BlockService service;

    public String createMapUrl (String dimension) {
        return "./map.xhtml?dimension=" + dimension;
    }

    public String normaliseDimensionName (String dimension) {
        if (dimension.contains(":")) {
            StringBuilder formattedName = new StringBuilder();
            boolean pastNamespace = false;
            boolean capitalise = true;

            for (int i = 0; i < dimension.length(); ++i) {
                char character = dimension.charAt(i);

                if (pastNamespace) {
                    if (character == '_') {
                        character = ' ';
                        capitalise = true;
                    }
                    else if (capitalise) {
                        character = Character.toUpperCase(character);
                        capitalise = false;
                    }

                    formattedName.append(character);
                }
                else if (character == ':') {
                    pastNamespace = true;
                }
            }

            return formattedName.toString();
        }
        return dimension;
    }

    public List<String> getDimensions () {
        return this.service.getDimensions();
    }
}
