package magasin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.util.Arrays;

public class ClientTest {
    
    private Class<?> getClasse() {
        try {
            return Class.forName("magasin.Client");
        } catch (ClassNotFoundException e) {
            fail("La classe Client n'existe pas");
            return null;
        }
    }

    @Test
    public void testAttributsExistent() {
        Class<?> classe = getClasse();
        Field[] fields = classe.getDeclaredFields();
        
        // Vérifie que tous les attributs requis existent
        assertTrue(Arrays.stream(fields).anyMatch(f -> f.getName().equals("nom")), "L'attribut 'nom' est manquant");
        assertTrue(Arrays.stream(fields).anyMatch(f -> f.getName().equals("email")), "L'attribut 'email' est manquant");
        
        // Vérifie les types des attributs
        try {
            assertEquals(String.class, classe.getDeclaredField("nom").getType(), "L'attribut 'nom' doit être de type String");
            assertEquals(String.class, classe.getDeclaredField("email").getType(), "L'attribut 'email' doit être de type String");
        } catch (NoSuchFieldException e) {
            fail("Un attribut requis est manquant");
        }
    }

    @Test
    public void testAttributsPrives() {
        Field[] fields = getClasse().getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()), 
                "L'attribut '" + field.getName() + "' doit être privé");
        }
    }

    @Test
    public void testConstructeurExiste() {
        try {
            Constructor<?> constructeur = getClasse().getConstructor(String.class, String.class);
            assertTrue(Modifier.isPublic(constructeur.getModifiers()), "Le constructeur doit être public");
        } catch (NoSuchMethodException e) {
            fail("Le constructeur avec les paramètres (String, String) est manquant");
        }
    }

    @Test
    public void testGettersExistent() {
        Class<?> classe = getClasse();
        Method[] methods = classe.getDeclaredMethods();
        
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("getNom")), "La méthode getNom() est manquante");
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("getEmail")), "La méthode getEmail() est manquante");
        
        try {
            assertEquals(String.class, classe.getMethod("getNom").getReturnType(), "getNom() doit retourner un String");
            assertEquals(String.class, classe.getMethod("getEmail").getReturnType(), "getEmail() doit retourner un String");
        } catch (NoSuchMethodException e) {
            fail("Un getter requis est manquant");
        }
    }

    @Test
    public void testSettersExistent() {
        Class<?> classe = getClasse();
        Method[] methods = classe.getDeclaredMethods();
        
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("setNom")), "La méthode setNom() est manquante");
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("setEmail")), "La méthode setEmail() est manquante");
        
        try {
            Method setNom = classe.getDeclaredMethod("setNom", String.class);
            Method setEmail = classe.getDeclaredMethod("setEmail", String.class);
            
            assertTrue(Modifier.isPublic(setNom.getModifiers()) || Modifier.isProtected(setNom.getModifiers()), 
                "setNom() doit être public ou protected");
            assertTrue(Modifier.isPublic(setEmail.getModifiers()) || Modifier.isProtected(setEmail.getModifiers()), 
                "setEmail() doit être public ou protected");
            
            assertEquals(void.class, setNom.getReturnType(), "setNom() doit être de type void");
            assertEquals(void.class, setEmail.getReturnType(), "setEmail() doit être de type void");
        } catch (NoSuchMethodException e) {
            fail("Un setter requis est manquant");
        }
    }

    @Test
    public void testMethodeAfficherDetailsExiste() {
        try {
            Method method = getClasse().getMethod("afficherDetails");
            assertEquals(void.class, method.getReturnType(), "afficherDetails() doit être de type void");
            assertTrue(Modifier.isPublic(method.getModifiers()), "afficherDetails() doit être public");
        } catch (NoSuchMethodException e) {
            fail("La méthode afficherDetails() est manquante");
        }
    }

    @Test
    public void testAfficherDetailsContenu() {
        try {
            Class<?> classe = getClasse();
            Constructor<?> constructeur = classe.getConstructor(String.class, String.class);
            Method afficherDetails = classe.getMethod("afficherDetails");
            
            // Redirect System.out to capture printed output
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            System.setOut(new java.io.PrintStream(out));

            // Create a test client using reflection
            Object client = constructeur.newInstance("Test", "test@email.com");
            afficherDetails.invoke(client);

            // Get the printed output
            String output = out.toString().trim();

            // Verify that all attributes are present in the output
            assertTrue(output.contains("Test"), "L'affichage doit contenir le nom");
            assertTrue(output.contains("test@email.com"), "L'affichage doit contenir l'email");

            // Restore normal System.out
            System.setOut(System.out);
        } catch (Exception e) {
            fail("Exception lors du test d'affichage: " + e.getMessage());
        }
    }
} 