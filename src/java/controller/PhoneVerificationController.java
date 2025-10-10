package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;

/**
 * Phone Verification Controller
 * Checks if a user exists with the given country code and phone number
 * 
 * @author Laky
 */
@WebServlet(name = "PhoneVerificationController", urlPatterns = {"/PhoneVerificationController"})
public class PhoneVerificationController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response content type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Enable CORS for React Native app
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        
        try {
            // Read JSON request body
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
            
            String jsonString = jsonBuffer.toString();
            System.out.println("Received JSON: " + jsonString);
            
            // Parse JSON request
            JsonObject requestJson = gson.fromJson(jsonString, JsonObject.class);
            
            String countryCode = null;
            String phoneNumber = null;
            
            if (requestJson.has("countryCode")) {
                countryCode = requestJson.get("countryCode").getAsString();
            }
            if (requestJson.has("phoneNumber")) {
                phoneNumber = requestJson.get("phoneNumber").getAsString();
            }

            // Validation
            if (countryCode == null || countryCode.trim().isEmpty()) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Country code is required.");
                responseObject.addProperty("exists", false);
            } else if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Phone number is required.");
                responseObject.addProperty("exists", false);
            } else {
                // Check if user exists in database
                SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
                Session session = sessionFactory.openSession();
                
                try {
                    Criteria criteria = session.createCriteria(User.class);
                    criteria.add(Restrictions.eq("countryCode", countryCode.trim()));
                    criteria.add(Restrictions.eq("contactNo", phoneNumber.trim()));

                    User user = (User) criteria.uniqueResult();
                    
                    if (user != null) {
                        // User exists
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "User found successfully.");
                        responseObject.addProperty("exists", true);
                        responseObject.addProperty("userId", user.getId());
                        
                        // Add user details to response
                        JsonObject userJson = new JsonObject();
                        userJson.addProperty("id", user.getId());
                        userJson.addProperty("firstName", user.getFirstName());
                        userJson.addProperty("lastName", user.getLastName());
                        userJson.addProperty("countryCode", user.getCountryCode());
                        userJson.addProperty("contactNo", user.getContactNo());
                        userJson.addProperty("status", user.getStatus().toString());
                        userJson.addProperty("createdAt", user.getCreatedAt().toString());
                        userJson.addProperty("updatedAt", user.getUpdatedAt().toString());
                        
                        responseObject.add("user", userJson);
                    } else {
                        // User doesn't exist
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "User not found. Proceed with registration.");
                        responseObject.addProperty("exists", false);
                    }
                } catch (Exception e) {
                    System.err.println("Database error: " + e.getMessage());
                    e.printStackTrace();
                    responseObject.addProperty("status", false);
                    responseObject.addProperty("message", "Database error occurred.");
                    responseObject.addProperty("exists", false);
                } finally {
                    if (session != null && session.isOpen()) {
                        session.close();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("General error: " + e.getMessage());
            e.printStackTrace();
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "An error occurred while processing your request.");
            responseObject.addProperty("exists", false);
        }

        // Send response
        response.getWriter().write(gson.toJson(responseObject));
        System.out.println("Response sent: " + gson.toJson(responseObject));
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle preflight requests for CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
