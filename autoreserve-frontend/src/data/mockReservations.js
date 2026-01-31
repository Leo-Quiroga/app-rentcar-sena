//Mock temporal de reservas de autos
export const mockReservations = [
  {
    id: "1",
    car: {
      id: "car1",
      name: "Toyota Corolla",
      image: "/src/assets/cars/corolla.jpg",
      category: "Sedán",
    },
    filters: {
      pickupCity: "Bogotá",
      dropoffCity: "Medellín",
      startDate: "2025-10-01",
      endDate: "2025-10-05",
    },
    dias: 4,
    total: 500,
    createdAt: "2025-09-01T10:00:00Z",
  },
  {
    id: "2",
    car: {
      id: "car2",
      name: "Nissan X-Trail",
      image: "/src/assets/cars/xtrail.jpg",
      category: "SUV",
    },
    filters: {
      pickupCity: "Cartagena",
      dropoffCity: "Cartagena",
      startDate: "2025-09-15",
      endDate: "2025-09-20",
    },
    dias: 5,
    total: 750,
    createdAt: "2025-09-05T14:30:00Z",
  },
  {
    id: "3",
    car: {
      id: "car3",
      image: "/src/assets/cars/cx5.jpg",
      name: "Mazda CX-5",
      category: "SUV",
    },
    filters: {
      pickupCity: "Cali",
      dropoffCity: "Bogotá",
      startDate: "2025-09-18",
      endDate: "2025-09-22",
    },
    dias: 4,
    total: 880,
    createdAt: "2025-09-08T09:15:00Z",
  },
];
