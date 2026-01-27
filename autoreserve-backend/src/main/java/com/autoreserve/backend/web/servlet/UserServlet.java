package com.autoreserve.backend.web.servlet;

import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.domain.repository.RoleRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import org.springframework.web.context.support.WebApplicationContextUtils;
import java.io.IOException;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Override
    public void init() throws ServletException {
        var context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        this.userRepository = context.getBean(UserRepository.class);
        this.roleRepository = context.getBean(RoleRepository.class);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User user = new User();
            user.setFirstName(req.getParameter("firstName"));
            user.setLastName(req.getParameter("lastName"));
            user.setEmail(req.getParameter("email"));
            user.setPasswordHash(req.getParameter("passwordHash"));
            user.setPhone(req.getParameter("phone"));

            // Obtenemos el roleId enviado desde el select
            String roleIdStr = req.getParameter("roleId");
            if (roleIdStr != null) {
                Long roleId = Long.parseLong(roleIdStr);
                // Buscamos el rol en la DB y lo asignamos
                user.setRole(roleRepository.findById(roleId).orElse(null));
            }

            userRepository.save(user);
            resp.sendRedirect(req.getContextPath() + "/users");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al guardar usuario");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("users", userRepository.findAll());
        req.getRequestDispatcher("/WEB-INF/views/user-list.jsp").forward(req, resp);
    }
}