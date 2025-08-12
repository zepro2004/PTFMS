package businesslayer.services;

import java.util.List;

import dataaccesslayer.VehicleComponentDAO;
import transferobjects.VehicleComponentDTO;

/**
 * Business service for Vehicle Component operations in the PTFMS system.
 * Manages vehicle parts inventory, component lifecycle tracking,
 * replacement scheduling, and maintenance-related component operations.
 * Provides business logic for component management and integration.
 */
public class VehicleComponentService {
  private final VehicleComponentDAO componentDAO;

  /**
   * Initializes the VehicleComponentService with a new VehicleComponentDAO
   * instance.
   */
  public VehicleComponentService() {
    this.componentDAO = new VehicleComponentDAO();
  }

  /**
   * Add a new vehicle component
   * 
   * @param component the component to add
   * @return true if successful
   */
  public boolean addVehicleComponent(VehicleComponentDTO component) {
    return componentDAO.addVehicleComponent(component);
  }

  /**
   * Get component by ID
   * 
   * @param componentId the component ID
   * @return VehicleComponentDTO if found, null otherwise
   */
  public VehicleComponentDTO getVehicleComponentById(int componentId) {
    return componentDAO.findById(componentId);
  }

  /**
   * Get all components for a specific vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return list of components for the vehicle
   */
  public List<VehicleComponentDTO> getComponentsByVehicleId(int vehicleId) {
    return componentDAO.getComponentsByVehicleId(vehicleId);
  }

  /**
   * Get components by status
   * 
   * @param status the component status
   * @return list of components with the specified status
   */
  public List<VehicleComponentDTO> getComponentsByStatus(String status) {
    return componentDAO.getComponentsByStatus(status);
  }

  /**
   * Get components that need maintenance
   * 
   * @param threshold the usage threshold for maintenance
   * @return list of components needing maintenance
   */
  public List<VehicleComponentDTO> getComponentsNeedingMaintenance(double threshold) {
    return componentDAO.getComponentsNeedingMaintenance(threshold);
  }

  /**
   * Update usage hours for a component
   * 
   * @param componentId     the component ID
   * @param additionalHours additional hours to add
   * @return true if successful
   */
  public boolean updateUsageHours(int componentId, double additionalHours) {
    return componentDAO.updateUsageHours(componentId, additionalHours);
  }

  /**
   * Update vehicle component information
   * 
   * @param component the updated component
   * @return true if successful
   */
  public boolean updateVehicleComponent(VehicleComponentDTO component) {
    return componentDAO.updateVehicleComponent(component);
  }

  /**
   * Delete a vehicle component
   * 
   * @param componentId the component ID
   * @return true if successful
   */
  public boolean deleteVehicleComponent(int componentId) {
    return componentDAO.deleteVehicleComponent(componentId);
  }

  /**
   * Check if a component needs maintenance based on usage hours
   * 
   * @param componentId          the component ID
   * @param maintenanceThreshold the threshold for maintenance
   * @return true if component needs maintenance
   */
  public boolean needsMaintenance(int componentId, double maintenanceThreshold) {
    VehicleComponentDTO component = getVehicleComponentById(componentId);
    if (component != null) {
      return component.getUsageHours().compareTo(java.math.BigDecimal.valueOf(maintenanceThreshold)) >= 0;
    }
    return false;
  }

  /**
   * Get component count for a vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return number of components for the vehicle
   */
  public int getComponentCountForVehicle(int vehicleId) {
    List<VehicleComponentDTO> components = getComponentsByVehicleId(vehicleId);
    return components != null ? components.size() : 0;
  }

  /**
   * Get operational components for a vehicle (status = 'Operational')
   * 
   * @param vehicleId the vehicle ID
   * @return list of operational components
   */
  public List<VehicleComponentDTO> getOperationalComponents(int vehicleId) {
    List<VehicleComponentDTO> allComponents = getComponentsByVehicleId(vehicleId);
    return allComponents.stream()
        .filter(component -> "Operational".equalsIgnoreCase(component.getStatus()))
        .collect(java.util.stream.Collectors.toList());
  }

  /**
   * Mark component as needing maintenance
   * 
   * @param componentId the component ID
   * @return true if successful
   */
  public boolean markForMaintenance(int componentId) {
    VehicleComponentDTO component = getVehicleComponentById(componentId);
    if (component != null) {
      component.setStatus("Maintenance Required");
      return updateVehicleComponent(component);
    }
    return false;
  }

  /**
   * Mark component as operational after maintenance
   * 
   * @param componentId the component ID
   * @return true if successful
   */
  public boolean markAsOperational(int componentId) {
    VehicleComponentDTO component = getVehicleComponentById(componentId);
    if (component != null) {
      component.setStatus("Operational");
      return updateVehicleComponent(component);
    }
    return false;
  }
}
