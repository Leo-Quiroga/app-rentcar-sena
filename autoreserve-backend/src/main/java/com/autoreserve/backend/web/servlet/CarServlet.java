package com.autoreserve.backend.web.servlet;

import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.CarStatus;
import com.autoreserve.backend.domain.repository.BranchRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.CategoryRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import org.springframework.web.context.support.WebApplicationContextUtils;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(urlPatterns = {"/cars", "/cars/new", "/cars/delete"})
public class CarServlet extends HttpServlet {

    private CarRepository carRepository;
    private CategoryRepository categoryRepository;
    private BranchRepository branchRepository;

    @Override
    public void init() throws ServletException {
        var context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        this.carRepository = context.getBean(CarRepository.class);
        this.categoryRepository = context.getBean(CategoryRepository.class);
        this.branchRepository = context.getBean(BranchRepository.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/cars/new".equals(path)) {
            // Cargar listas para los dropdowns del formulario
            req.setAttribute("categories", categoryRepository.findAll());
            req.setAttribute("branches", branchRepository.findAll());
            req.getRequestDispatcher("/WEB-INF/views/car-form.jsp").forward(req, resp);
        } else {
            // Listar autos
            req.setAttribute("cars", carRepository.findAll());
            req.getRequestDispatcher("/WEB-INF/views/car-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            // Lógica de Borrado
            Long carId = Long.parseLong(req.getParameter("id"));
            try {
                carRepository.deleteById(carId);
            } catch (Exception e) {
                // Manejar error si el auto tiene reservas (Foreign Key constraint)
                e.printStackTrace();
            }
        } else {
            // Lógica de Creación
            try {
                Car car = new Car();
                car.setBrand(req.getParameter("brand"));
                car.setModel(req.getParameter("model"));
                car.setYear(Integer.parseInt(req.getParameter("year")));
                car.setPlate(req.getParameter("plate"));
                car.setPricePerDay(new BigDecimal(req.getParameter("pricePerDay")));
                car.setStatus(CarStatus.valueOf(req.getParameter("status")));

                // Asignar relaciones
                Long catId = Long.parseLong(req.getParameter("categoryId"));
                Long branchId = Long.parseLong(req.getParameter("branchId"));

                car.setCategory(categoryRepository.findById(catId).orElseThrow());
                car.setBranch(branchRepository.findById(branchId).orElseThrow());

                carRepository.save(car);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        resp.sendRedirect(req.getContextPath() + "/cars");
    }
}