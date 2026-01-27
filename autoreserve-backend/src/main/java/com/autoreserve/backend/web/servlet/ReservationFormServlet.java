package com.autoreserve.backend.web.servlet;

import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import org.springframework.web.context.support.WebApplicationContextUtils;
import java.io.IOException;

@WebServlet("/reservations/new")
public class ReservationFormServlet extends HttpServlet {
    private UserRepository userRepository;
    private CarRepository carRepository;

    @Override
    public void init() throws ServletException {
        var context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        this.userRepository = context.getBean(UserRepository.class);
        this.carRepository = context.getBean(CarRepository.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("users", userRepository.findAll());
        req.setAttribute("cars", carRepository.findAll());
        req.getRequestDispatcher("/WEB-INF/views/reservation-form.jsp").forward(req, resp);
    }
}