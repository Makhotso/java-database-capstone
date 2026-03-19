import { API_BASE_URL } from "../config/config.js";

const APPOINTMENT_API = `${API_BASE_URL}/appointments`;

// Get all appointments (Doctor)
export async function getAllAppointments(date, patientName, token) {
  try {
    const response = await fetch(`${APPOINTMENT_API}/${date}/${patientName}`, {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    if (!response.ok) {
      throw new Error("Failed to fetch appointments");
    }

    return await response.json();

  } catch (error) {
    console.error("Error fetching appointments:", error);
    throw error;
  }
}

// Book appointment
export async function bookAppointment(appointment, token) {
  try {
    const response = await fetch(`${APPOINTMENT_API}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify(appointment)
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Something went wrong"
    };

  } catch (error) {
    console.error("Error while booking appointment:", error);
    return {
      success: false,
      message: "Network error. Please try again later."
    };
  }
}

// Update appointment
export async function updateAppointment(appointment, token) {
  try {
    const response = await fetch(`${APPOINTMENT_API}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify(appointment)
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Something went wrong"
    };

  } catch (error) {
    console.error("Error while updating appointment:", error);
    return {
      success: false,
      message: "Network error. Please try again later."
    };
  }
}