package magasin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.util.Arrays;

public class ProduitTest {
    
    private Class<?> getClasse() {
        try {
            return Class.forName("magasin.Produit");
        } catch (ClassNotFoundException e) {
            fail("La classe Produit n'existe pas");
            return null;
        }
    }

    @Test
    public void testAttributsExistent() {
        Class<?> classe = getClasse();
        Field[] fields = classe.getDeclaredFields();
        
        // Vérifie que tous les attributs requis existent
        assertTrue(Arrays.stream(fields).anyMatch(f -> f.getName().equals("id")), "L'attribut 'id' est manquant");
        assertTrue(Arrays.stream(fields).anyMatch(f -> f.getName().equals("nom")), "L'attribut 'nom' est manquant");
        assertTrue(Arrays.stream(fields).anyMatch(f -> f.getName().equals("prix")), "L'attribut 'prix' est manquant");
        
        // Vérifie les types des attributs
        try {
            assertEquals(int.class, classe.getDeclaredField("id").getType(), "L'attribut 'id' doit être de type int");
            assertEquals(String.class, classe.getDeclaredField("nom").getType(), "L'attribut 'nom' doit être de type String");
            assertEquals(double.class, classe.getDeclaredField("prix").getType(), "L'attribut 'prix' doit être de type double");
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
            Constructor<?> constructeur = getClasse().getConstructor(int.class, String.class, double.class);
            assertTrue(Modifier.isPublic(constructeur.getModifiers()), "Le constructeur doit être public");
        } catch (NoSuchMethodException e) {
            fail("Le constructeur avec les paramètres (int, String, double) est manquant");
        }
    }

    @Test
    public void testGettersExistent() {
        Class<?> classe = getClasse();
        Method[] methods = classe.getDeclaredMethods();
        
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("getId")), "La méthode getId() est manquante");
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("getNom")), "La méthode getNom() est manquante");
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("getPrix")), "La méthode getPrix() est manquante");
        
        try {
            assertEquals(int.class, classe.getMethod("getId").getReturnType(), "getId() doit retourner un int");
            assertEquals(String.class, classe.getMethod("getNom").getReturnType(), "getNom() doit retourner un String");
            assertEquals(double.class, classe.getMethod("getPrix").getReturnType(), "getPrix() doit retourner un double");
        } catch (NoSuchMethodException e) {
            fail("Un getter requis est manquant");
        }
    }

    @Test
    public void testSettersExistent() {
        Class<?> classe = getClasse();
        Method[] methods = classe.getDeclaredMethods();
        
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("setPrix")), "La méthode setPrix() est manquante");
        
        try {
            Method setPrix = classe.getDeclaredMethod("setPrix", double.class);
            
            assertTrue(Modifier.isProtected(setPrix.getModifiers()), 
                "setPrix() doit être protected");
            
            assertEquals(void.class, setPrix.getReturnType(), "setPrix() doit être de type void");
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
            Constructor<?> constructeur = classe.getConstructor(int.class, String.class, double.class);
            Method afficherDetails = classe.getMethod("afficherDetails");
            
            // Redirect System.out to capture printed output
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            System.setOut(new java.io.PrintStream(out));

            // Create a test product using reflection
            Object produit = constructeur.newInstance(1, "Test", 10.5);
            afficherDetails.invoke(produit);

            // Get the printed output
            String output = out.toString().trim();

            // Verify that all attributes are present in the output
            assertTrue(output.contains("1"), "L'affichage doit contenir l'ID");
            assertTrue(output.contains("Test"), "L'affichage doit contenir le nom");
            assertTrue(output.contains("10.5"), "L'affichage doit contenir le prix");

            // Restore normal System.out
            System.setOut(System.out);
        } catch (Exception e) {
            fail("Exception lors du test d'affichage: " + e.getMessage());
        }
    }
} 