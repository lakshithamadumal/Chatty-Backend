package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

/**
 *
 * @author Laky
 */
@MultipartConfig
@WebServlet(name = "UserController", urlPatterns = {"/UserController"})
public class UserController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String countryCode = request.getParameter("countryCode");
        String contactNo = request.getParameter("contactNo");
        Part profileImage = request.getPart("profileImage");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        // Validation
        if (firstName == null || firstName.isEmpty()) {
            responseObject.addProperty("message", "First name is required.");
        } else if (lastName == null || lastName.isEmpty()) {
            responseObject.addProperty("message", "Last name is required.");
        } else if (countryCode == null || countryCode.isEmpty()) {
            responseObject.addProperty("message", "Country code is required.");
        } else if (contactNo == null || contactNo.isEmpty()) {
            responseObject.addProperty("message", "Phone number is required.");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria criteriaUser = s.createCriteria(User.class);
            criteriaUser.add(Restrictions.eq("countryCode", countryCode));
            criteriaUser.add(Restrictions.eq("contactNo", contactNo));

            User userList = (User) criteriaUser.uniqueResult();

            if (userList != null) {
                responseObject.addProperty("message", "This Contact Number Already Exist");
            } else {
                User user = new User(firstName, lastName, countryCode, contactNo);
                int id = (int) s.save(user);
                s.beginTransaction().commit();
                s.close();

                responseObject.add("user", gson.toJsonTree(user));

                String appPath = getServletContext().getRealPath("");
                String newPath = appPath.replace("build" + File.separator + "web", "web" + File.separator + "profile-images");
                File profileFolder = new File(newPath, String.valueOf(id));
                profileFolder.mkdirs();
                File file1 = new File(profileFolder, "profile.png");
                Files.copy(profileImage.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

                responseObject.addProperty("status", true);
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
