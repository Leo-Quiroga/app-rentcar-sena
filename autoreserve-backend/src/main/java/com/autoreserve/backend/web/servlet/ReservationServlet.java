package com.autoreserve.backend.web.servlet;

import com.autoreserve.backend.domain.entity.Reservation;
import com.autoreserve.backend.domain.entity.ReservationStatus;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.ReservationRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@WebServlet("/reservations")
public class ReservationServlet extends HttpServlet {

    private ReservationRepository reservationRepository;
    private UserRepository userRepository;
    private CarRepository carRepository;

    @Override
    public void init() throws ServletException {
        var context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        this.reservationRepository = context.getBean(ReservationRepository.class);
        this.userRepository = context.getBean(UserRepository.class);
        this.carRepository = context.getBean(CarRepository.class);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Reservation r = new Reservation();
        r.setUser(userRepository.findById(Long.parseLong(req.getParameter("userId"))).orElseThrow());
        r.setCar(carRepository.findById(Long.parseLong(req.getParameter("carId"))).orElseThrow());

        // Procesar fechas obligatorias según la Entity
        r.setStartDate(LocalDate.parse(req.getParameter("startDate")));
        r.setEndDate(LocalDate.parse(req.getParameter("endDate")));
        r.setTotalAmount(new BigDecimal(req.getParameter("totalAmount")));
        r.setStatus(ReservationStatus.CONFIRMED);

        reservationRepository.save(r);

        resp.sendRedirect(req.getContextPath() + "/reservations");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("reservations", reservationRepository.findAll());
        req.getRequestDispatcher("/WEB-INF/views/reservation-list.jsp").forward(req, resp);
    }
}