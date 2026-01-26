/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: "#2563eb", // azul principal
          dark: "#1e40af",    // azul hover
        },
        secondary: {
          DEFAULT: "#facc15", // amarillo principal
          dark: "#eab308",    // amarillo hover
        },
        neutral: {
          light: "#f3f4f6",   // fondo claro
          dark: "#1f2937",    // texto oscuro
        },
      },
      fontFamily: {
        sans: ["Inter", "system-ui", "sans-serif"],
        display: ["Poppins", "sans-serif"],
      },
      spacing: {
        18: "72px",
        22: "88px",
      },

      // Animaciones personalizadas
      keyframes: {
        fadeIn: {
          "0%": { opacity: 0, transform: "scale(0.8)" },
          "100%": { opacity: 1, transform: "scale(1)" },
        },
        fadeOut: {
          "0%": { opacity: 1 },
          "100%": { opacity: 0 },
        },
      },
      animation: {
        fadeIn: "fadeIn 1s ease-out", // prueba 1s para ver la animación
        fadeOut: "fadeOut 1s ease-in",
      },
    },
  },
  plugins: [],
}
