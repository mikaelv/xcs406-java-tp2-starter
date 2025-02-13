package magasin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.ArrayList;


public class MagasinTest {
    
    private Class<?> getClasse() {
        try {
            return Class.forName("magasin.Magasin");
        } catch (ClassNotFoundException e) {
            fail("La classe Magasin n'existe pas");
            return null;
        }
    }

    @Test
    public void testAttributsExistent() {
        Class<?> classe = getClasse();
        Field[] fields = classe.getDeclaredFields();
        
        // Vérifie que tous les attributs requis existent
        assertTrue(Arrays.stream(fields).anyMatch(f -> f.getName().equals("produits")), "L'attribut 'produits' est manquant");
        
        // Vérifie les types des attributs
        try {
            Field produitsField = classe.getDeclaredField("produits");
            assertEquals(ArrayList.class, produitsField.getType(), "L'attribut 'produits' doit être de type ArrayList");
            
            // Vérifie que c'est bien une List de Produit
            Type genericType = produitsField.getGenericType();
            assertTrue(genericType.toString().contains("Produit"), 
                "L'attribut 'produits' doit être une List<Produit>");
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
            Constructor<?> constructeur = getClasse().getConstructor();
            assertTrue(Modifier.isPublic(constructeur.getModifiers()), "Le constructeur doit être public");
        } catch (NoSuchMethodException e) {
            fail("Le constructeur sans paramètres est manquant");
        }
    }

    @Test
    public void testMethodesExistent() {
        Class<?> classe = getClasse();
        Method[] methods = classe.getDeclaredMethods();
        
        // Vérifie l'existence des méthodes
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("ajouterProduit")), 
            "La méthode ajouterProduit() est manquante");
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("afficherProduitsDisponibles")), 
            "La méthode afficherProduitsDisponibles() est manquante");
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("trouverProduitParId")), 
            "La méthode trouverProduitParId() est manquante");
        
        try {
            // Vérifie les signatures des méthodes
            Method ajouterProduit = classe.getMethod("ajouterProduit", Class.forName("magasin.Produit"));
            Method afficherProduits = classe.getMethod("afficherProduitsDisponibles");
            Method trouverProduit = classe.getMethod("trouverProduitParId", int.class);
            
            assertTrue(Modifier.isPublic(ajouterProduit.getModifiers()), 
                "ajouterProduit() doit être public");
            assertTrue(Modifier.isPublic(afficherProduits.getModifiers()), 
                "afficherProduitsDisponibles() doit être public");
            assertTrue(Modifier.isPublic(trouverProduit.getModifiers()), 
                "trouverProduitParId() doit être public");
            
            assertEquals(void.class, ajouterProduit.getReturnType(), 
                "ajouterProduit() doit retourner void");
            assertEquals(void.class, afficherProduits.getReturnType(), 
                "afficherProduitsDisponibles() doit retourner void");
            assertEquals(Class.forName("magasin.Produit"), trouverProduit.getReturnType(), 
                "trouverProduitParId() doit retourner un Produit");
            
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            fail("Erreur lors de la vérification des signatures des méthodes: " + e.getMessage());
        }
    }

    @Test
    public void testTrouverProduitParId() {
        try {
            Class<?> classeMagasin = getClasse();
            Class<?> classeProduit = Class.forName("magasin.Produit");
            
            // Create test objects
            Constructor<?> constructeurMagasin = classeMagasin.getConstructor();
            Constructor<?> constructeurProduit = classeProduit.getConstructor(int.class, String.class, double.class);
            
            Object magasin = constructeurMagasin.newInstance();
            Object produit = constructeurProduit.newInstance(1, "TestProduit", 10.0);
            
            // Add product to magasin
            Method ajouterProduit = classeMagasin.getMethod("ajouterProduit", classeProduit);
            ajouterProduit.invoke(magasin, produit);
            
            // Test finding the product
            Method trouverProduit = classeMagasin.getMethod("trouverProduitParId", int.class);
            Object produitTrouve = trouverProduit.invoke(magasin, 1);
            
            assertNotNull(produitTrouve, "Le produit devrait être trouvé");
            
            // Verify it's the same product
            Method getId = classeProduit.getMethod("getId");
            assertEquals(1, getId.invoke(produitTrouve), 
                "Le produit trouvé devrait avoir le même ID");
            
        } catch (Exception e) {
            fail("Exception lors du test de trouverProduitParId: " + e.getMessage());
        }
    }
} 