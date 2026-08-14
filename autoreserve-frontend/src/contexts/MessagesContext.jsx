import { createContext, useContext, useState, useEffect, useCallback, useRef } from "react";
import { getUnreadCount, getAdminTickets, getMyTickets } from "../api/contactApi";
import { useAuth } from "../auth/useAuth";

const MessagesContext = createContext();

const POLL_INTERVAL = 15000;

export function MessagesProvider({ children }) {
  const { user } = useAuth();
  const [tickets, setTickets] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const intervalRef = useRef(null);

  const refresh = useCallback(async () => {
    if (!user) return;
    try {
      const isAdmin = user.role === "ADMIN";
      const [fetchedTickets, countData] = await Promise.all([
        isAdmin ? getAdminTickets() : getMyTickets(),
        getUnreadCount(),
      ]);
      setTickets(fetchedTickets);
      setUnreadCount(countData.unreadCount || 0);
    } catch {
      // silencioso — no interrumpir la UI por un fallo de polling
    }
  }, [user]);

  // Carga inicial + arranque del intervalo
  useEffect(() => {
    if (!user) {
      setTickets([]);
      setUnreadCount(0);
      return;
    }

    setLoading(true);
    refresh().finally(() => setLoading(false));

    intervalRef.current = setInterval(refresh, POLL_INTERVAL);
    return () => clearInterval(intervalRef.current);
  }, [user, refresh]);

  return (
    <MessagesContext.Provider value={{ tickets, unreadCount, loading, refresh }}>
      {children}
    </MessagesContext.Provider>
  );
}

export function useMessages() {
  const ctx = useContext(MessagesContext);
  if (!ctx) throw new Error("useMessages must be used within MessagesProvider");
  return ctx;
}
