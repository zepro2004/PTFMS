package businesslayer.services;

import java.util.ArrayList;
import java.util.List;

import businesslayer.VehicleBuilder;
import dataaccesslayer.VehicleDAO;
import transferobjects.VehicleDTO;

/**
 * Business service for Vehicle operations in the PTFMS system.
 * Provides comprehensive vehicle management functionality including
 * CRUD operations, validation, and business logic coordination.
 * Implements the Builder pattern for vehicle creation and manages
 * vehicle lifecycle and status tracking.
 */
public class VehicleService {
    private VehicleDAO vehicleDAO;

    /**
     * Constructor
     */
    public VehicleService() {
        this.vehicleDAO = new VehicleDAO();
    }

    /**
     * Add a new vehicle
     * 
     * @param vehicle the vehicle to add
     * @return true if successful
     */
    public boolean addVehicle(VehicleDTO vehicle) {
        // Validate required fields
        if (vehicle.getVin() == null || vehicle.getVin().trim().isEmpty()) {
            return false;
        }
        if (vehicle.getVehicleNumber() == null || vehicle.getVehicleNumber().trim().isEmpty()) {
            return false;
        }

        // Check if VIN already exists
        if (vehicleDAO.findByVin(vehicle.getVin()) != null) {
            return false;
        }

        // Check if vehicle number already exists
        List<VehicleDTO> existingVehicles = vehicleDAO.getAllVehicles();
        for (VehicleDTO existing : existingVehicles) {
            if (existing.getVehicleNumber().equals(vehicle.getVehicleNumber())) {
                return false;
            }
        }

        return vehicleDAO.addVehicle(vehicle);
    }

    /**
     * Update a vehicle
     * 
     * @param vehicle the vehicle to update
     * @return true if successful
     */
    public boolean updateVehicle(VehicleDTO vehicle) {
        // Validate required fields
        if (vehicle.getVehicleId() == 0) {
            return false;
        }
        if (vehicle.getVin() == null || vehicle.getVin().trim().isEmpty()) {
            return false;
        }
        if (vehicle.getVehicleNumber() == null || vehicle.getVehicleNumber().trim().isEmpty()) {
            return false;
        }

        // Check if VIN already exists for a different vehicle
        VehicleDTO existingByVin = vehicleDAO.findByVin(vehicle.getVin());
        if (existingByVin != null && existingByVin.getVehicleId() != vehicle.getVehicleId()) {
            return false;
        }

        // Check if vehicle number already exists for a different vehicle
        List<VehicleDTO> existingVehicles = vehicleDAO.getAllVehicles();
        for (VehicleDTO existing : existingVehicles) {
            if (existing.getVehicleNumber().equals(vehicle.getVehicleNumber())
                    && existing.getVehicleId() != vehicle.getVehicleId()) {
                return false;
            }
        }

        return vehicleDAO.updateVehicle(vehicle);
    }

    /**
     * Delete a vehicle
     * 
     * @param vehicleId the ID of the vehicle to delete
     * @return true if successful
     */
    public boolean deleteVehicle(int vehicleId) {
        if (vehicleId <= 0) {
            return false;
        }
        return vehicleDAO.deleteVehicle(vehicleId);
    }

    /**
     * Get a vehicle by ID
     * 
     * @param vehicleId the vehicle ID
     * @return the vehicle or null if not found
     */
    public VehicleDTO getVehicleById(int vehicleId) {
        if (vehicleId <= 0) {
            return null;
        }
        return vehicleDAO.findById(vehicleId);
    }

    /**
     * Get a vehicle by VIN
     * 
     * @param vin the vehicle VIN
     * @return the vehicle or null if not found
     */
    public VehicleDTO getVehicleByVin(String vin) {
        if (vin == null || vin.trim().isEmpty()) {
            return null;
        }
        return vehicleDAO.findByVin(vin);
    }

    /**
     * Get all vehicles
     * 
     * @return list of all vehicles
     */
    public List<VehicleDTO> getAllVehicles() {
        return vehicleDAO.getAllVehicles();
    }

    /**
     * Get vehicles by status
     * 
     * @param status the vehicle status
     * @return list of vehicles with the specified status
     */
    public List<VehicleDTO> getVehiclesByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return getAllVehicles();
        }
        return vehicleDAO.findByStatus(status);
    }

    /**
     * Get vehicles by type
     * 
     * @param type the vehicle type
     * @return list of vehicles of the specified type
     */
    public List<VehicleDTO> getVehiclesByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return getAllVehicles();
        }
        // Filter by type from all vehicles since there's no specific DAO method
        List<VehicleDTO> allVehicles = getAllVehicles();
        List<VehicleDTO> filteredVehicles = new ArrayList<>();
        for (VehicleDTO vehicle : allVehicles) {
            if (type.equals(vehicle.getVehicleType())) {
                filteredVehicles.add(vehicle);
            }
        }
        return filteredVehicles;
    }

    /**
     * Create a new vehicle using the builder pattern
     * 
     * @return a new VehicleBuilder instance
     */
    public VehicleBuilder createVehicleBuilder() {
        return new VehicleBuilder();
    }

    /**
     * Create a new vehicle using the builder pattern (alternative method name)
     * 
     * @return a new VehicleBuilder instance
     */
    public VehicleBuilder createVehicle() {
        return new VehicleBuilder();
    }

    /**
     * Validate vehicle data
     * 
     * @param vehicle the vehicle to validate
     * @return true if valid
     */
    public boolean validateVehicle(VehicleDTO vehicle) {
        if (vehicle == null) {
            return false;
        }

        // Required fields
        if (vehicle.getVin() == null || vehicle.getVin().trim().isEmpty()) {
            return false;
        }
        if (vehicle.getVehicleNumber() == null || vehicle.getVehicleNumber().trim().isEmpty()) {
            return false;
        }
        if (vehicle.getVehicleType() == null || vehicle.getVehicleType().trim().isEmpty()) {
            return false;
        }
        if (vehicle.getStatus() == null || vehicle.getStatus().trim().isEmpty()) {
            return false;
        }

        // VIN should be 17 characters
        if (vehicle.getVin().length() != 17) {
            return false;
        }

        // Validate max passengers is positive
        if (vehicle.getMaxPassengers() <= 0) {
            return false;
        }

        return true;
    }

    /**
     * Search vehicles by various criteria
     * 
     * @param searchTerm the search term
     * @return list of matching vehicles
     */
    public List<VehicleDTO> searchVehicles(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllVehicles();
        }

        // Filter vehicles that match the search term
        List<VehicleDTO> allVehicles = getAllVehicles();
        List<VehicleDTO> filteredVehicles = new ArrayList<>();
        String searchLower = searchTerm.toLowerCase();

        for (VehicleDTO vehicle : allVehicles) {
            if (vehicle.getVin().toLowerCase().contains(searchLower) ||
                    vehicle.getVehicleNumber().toLowerCase().contains(searchLower) ||
                    vehicle.getMake().toLowerCase().contains(searchLower) ||
                    vehicle.getModel().toLowerCase().contains(searchLower)) {
                filteredVehicles.add(vehicle);
            }
        }

        return filteredVehicles;
    }

    /**
     * Update vehicle status
     * 
     * @param vehicleId the vehicle ID
     * @param status    the new status
     * @return true if successful
     */
    public boolean updateVehicleStatus(int vehicleId, String status) {
        if (vehicleId <= 0 || status == null || status.trim().isEmpty()) {
            return false;
        }

        VehicleDTO vehicle = getVehicleById(vehicleId);
        if (vehicle == null) {
            return false;
        }

        vehicle.setStatus(status);
        return vehicleDAO.updateVehicle(vehicle);
    }

    /**
     * Get count of vehicles by status
     * 
     * @param status the vehicle status
     * @return count of vehicles with the specified status
     */
    public int getVehicleCountByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return 0;
        }
        List<VehicleDTO> vehicles = getVehiclesByStatus(status);
        return vehicles != null ? vehicles.size() : 0;
    }
}
