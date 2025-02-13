package magasin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CommandeTest {
    
    private Class<?> getClasse() {
        try {
            return Class.forName("magasin.Commande");
        } catch (ClassNotFoundException e) {
            fail("La classe Commande n'existe pas");
            return null;
        }
    }

    @Test
    public void testAttributsExistent() {
        Class<?> classe = getClasse();
        Field[] fields = classe.getDeclaredFields();
        
        // Vérifie que tous les attributs requis existent
        assertTrue(Arrays.stream(fields).anyMatch(f -> f.getName().equals("client")), "L'attribut 'client' est manquant");
        assertTrue(Arrays.stream(fields).anyMatch(f -> f.getName().equals("produitsCommandes")), "L'attribut 'produitsCommandes' est manquant");
        assertTrue(Arrays.stream(fields).anyMatch(f -> f.getName().equals("total")), "L'attribut 'total' est manquant");
        assertTrue(Arrays.stream(fields).anyMatch(f -> f.getName().equals("dateHeure")), "L'attribut 'dateHeure' est manquant");
        
        // Vérifie les types des attributs
        try {
            assertEquals(Class.forName("magasin.Client"), classe.getDeclaredField("client").getType(), 
                "L'attribut 'client' doit être de type Client");
            assertEquals(ArrayList.class, classe.getDeclaredField("produitsCommandes").getType(), 
                "L'attribut 'produitsCommandes' doit être de type ArrayList");
            assertEquals(double.class, classe.getDeclaredField("total").getType(), 
                "L'attribut 'total' doit être de type double");
            assertEquals(LocalDateTime.class, classe.getDeclaredField("dateHeure").getType(), 
                "L'attribut 'dateHeure' doit être de type LocalDateTime");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
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
            Constructor<?> constructeur = getClasse().getConstructor(
                Class.forName("magasin.Client"), 
                Class.forName("magasin.Panier"),
                LocalDateTime.class
            );
            assertTrue(Modifier.isPublic(constructeur.getModifiers()), "Le constructeur doit être public");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            fail("Le constructeur avec les paramètres (Client, Panier, LocalDateTime) est manquant");
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
            Class<?> classeCommande = getClasse();
            Class<?> classeClient = Class.forName("magasin.Client");
            Class<?> classePanier = Class.forName("magasin.Panier");
            
            // Create test objects
            Constructor<?> constructeurClient = classeClient.getConstructor(String.class, String.class);
            Constructor<?> constructeurPanier = classePanier.getConstructor();
            Object client = constructeurClient.newInstance("TestClient", "test@email.com");
            Object panier = constructeurPanier.newInstance();
            LocalDateTime dateHeure = LocalDateTime.now();
            
            // Create the command
            Constructor<?> constructeurCommande = classeCommande.getConstructor(
                classeClient, classePanier, LocalDateTime.class);
            Object commande = constructeurCommande.newInstance(client, panier, dateHeure);
            
            // Redirect System.out to capture printed output
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            System.setOut(new java.io.PrintStream(out));

            // Call afficherDetails
            Method afficherDetails = classeCommande.getMethod("afficherDetails");
            afficherDetails.invoke(commande);

            // Get the printed output
            String output = out.toString().trim();

            // Verify that essential information is present
            assertTrue(output.contains("TestClient"), "L'affichage doit contenir le nom du client");
            assertTrue(output.contains(dateHeure.toString()), "L'affichage doit contenir la date");

            // Restore normal System.out
            System.setOut(System.out);
        } catch (Exception e) {
            fail("Exception lors du test d'affichage: " + e.getMessage());
        }
    }
} 