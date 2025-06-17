package com.maciu19.autobidder.api.model.enums;

import lombok.Getter;

public enum Feature implements EnumString {

    BLUETOOTH("bluetooth", FeatureCategory.AUDIO_TECHNOLOGY),
    USB_PORT("usb", FeatureCategory.AUDIO_TECHNOLOGY),
    AUXILIARY_INPUT("aux", FeatureCategory.AUDIO_TECHNOLOGY),
    APPLE_CARPLAY("carplay", FeatureCategory.AUDIO_TECHNOLOGY),
    ANDROID_AUTO("android_auto", FeatureCategory.AUDIO_TECHNOLOGY),
    NAVIGATION_SYSTEM("navigation_system", FeatureCategory.AUDIO_TECHNOLOGY),
    TOUCHSCREEN_DISPLAY("touchscreen_display", FeatureCategory.AUDIO_TECHNOLOGY),
    WIRELESS_PHONE_CHARGING("wireless_phone_charging", FeatureCategory.AUDIO_TECHNOLOGY),

    AIR_CONDITIONING("air_conditioning", FeatureCategory.COMFORT_EQUIPMENT),
    DUAL_ZONE_CLIMATE_CONTROL("dual_zone_climate_control", FeatureCategory.COMFORT_EQUIPMENT),
    FOUR_ZONE_CLIMATE_CONTROL("four_zone_climate_control", FeatureCategory.COMFORT_EQUIPMENT),
    HEATED_FRONT_SEATS("heated_front_seats", FeatureCategory.COMFORT_EQUIPMENT),
    HEATED_REAR_SEATS("heated_rear_seats", FeatureCategory.COMFORT_EQUIPMENT),
    VENTILATED_SEATS("ventilated_seats", FeatureCategory.COMFORT_EQUIPMENT),
    MASSAGE_SEATS("massage_seats", FeatureCategory.COMFORT_EQUIPMENT),
    POWER_ADJUSTABLE_SEATS("power_adjustable_seats", FeatureCategory.COMFORT_EQUIPMENT),
    MEMORY_SEATS("memory_seats", FeatureCategory.COMFORT_EQUIPMENT),
    HEATED_STEERING_WHEEL("heated_steering_wheel", FeatureCategory.COMFORT_EQUIPMENT),
    POWER_WINDOWS_FRONT("power_windows_front", FeatureCategory.COMFORT_EQUIPMENT),
    POWER_WINDOWS_REAR("power_windows_rear", FeatureCategory.COMFORT_EQUIPMENT),
    SUNROOF("sunroof", FeatureCategory.COMFORT_EQUIPMENT),
    PANORAMIC_ROOF("panoramic_roof", FeatureCategory.COMFORT_EQUIPMENT),
    KEYLESS_ENTRY("keyless_entry", FeatureCategory.COMFORT_EQUIPMENT),
    KEYLESS_GO("keyless_go", FeatureCategory.COMFORT_EQUIPMENT),
    POWER_TAILGATE("power_tailgate", FeatureCategory.COMFORT_EQUIPMENT),
    HEATED_MIRRORS("heated_mirrors", FeatureCategory.COMFORT_EQUIPMENT),
    POWER_FOLDING_MIRRORS("power_folding_mirrors", FeatureCategory.COMFORT_EQUIPMENT),
    RAIN_SENSOR("rain_sensor", FeatureCategory.COMFORT_EQUIPMENT),
    LIGHT_SENSOR("light_sensor", FeatureCategory.COMFORT_EQUIPMENT),

    REAR_PARKING_SENSORS("rear_parking_sensors", FeatureCategory.ELECTRONICS_ASSISTANCE),
    FRONT_PARKING_SENSORS("front_parking_sensors", FeatureCategory.ELECTRONICS_ASSISTANCE),
    REAR_VIEW_CAMERA("rear_view_camera", FeatureCategory.ELECTRONICS_ASSISTANCE),
    SURROUND_VIEW_CAMERAS("surround_view_cameras", FeatureCategory.ELECTRONICS_ASSISTANCE),
    PARKING_ASSIST("parking_assist", FeatureCategory.ELECTRONICS_ASSISTANCE),
    CRUISE_CONTROL("cruise_control", FeatureCategory.ELECTRONICS_ASSISTANCE),
    ADAPTIVE_CRUISE_CONTROL("adaptive_cruise_control", FeatureCategory.ELECTRONICS_ASSISTANCE),
    LANE_KEEP_ASSIST("lane_keep_assist", FeatureCategory.ELECTRONICS_ASSISTANCE),
    BLIND_SPOT_MONITOR("blind_spot_monitor", FeatureCategory.ELECTRONICS_ASSISTANCE),
    TRAFFIC_SIGN_RECOGNITION("traffic_sign_recognition", FeatureCategory.ELECTRONICS_ASSISTANCE),
    HEAD_UP_DISPLAY("head_up_display", FeatureCategory.ELECTRONICS_ASSISTANCE),
    AUTOMATIC_HEADLIGHTS("automatic_headlights", FeatureCategory.ELECTRONICS_ASSISTANCE),
    HIGH_BEAM_ASSIST("high_beam_assist", FeatureCategory.ELECTRONICS_ASSISTANCE),
    LED_HEADLIGHTS("led_headlights", FeatureCategory.ELECTRONICS_ASSISTANCE),
    BI_XENON_HEADLIGHTS("bi_xenon_headlights", FeatureCategory.ELECTRONICS_ASSISTANCE),
    ADAPTIVE_HEADLIGHTS("adaptive_headlights", FeatureCategory.ELECTRONICS_ASSISTANCE),

    ALL_WHEEL_DRIVE("awd", FeatureCategory.PERFORMANCE),
    AIR_SUSPENSION("air_suspension", FeatureCategory.PERFORMANCE),
    ADAPTIVE_SUSPENSION("adaptive_suspension", FeatureCategory.PERFORMANCE),
    DRIVE_MODE_SELECTOR("drive_mode_selector", FeatureCategory.PERFORMANCE),
    SPORT_EXHAUST_SYSTEM("sport_exhaust_system", FeatureCategory.PERFORMANCE),
    PERFORMANCE_BRAKES("performance_brakes", FeatureCategory.PERFORMANCE),
    PADDLE_SHIFTERS("paddle_shifters", FeatureCategory.PERFORMANCE),

    ABS("ABS", FeatureCategory.SAFETY),
    ESP("ESP", FeatureCategory.SAFETY),
    TRACTION_CONTROL("traction_control", FeatureCategory.SAFETY),
    EMERGENCY_BRAKE_ASSIST("emergency_brake_assist", FeatureCategory.SAFETY),
    AUTOMATIC_EMERGENCY_BRAKING("automatic_emergency_braking", FeatureCategory.SAFETY),
    FRONT_AIRBAGS("front_airbags", FeatureCategory.SAFETY),
    SIDE_AIRBAGS("side_airbags", FeatureCategory.SAFETY),
    CURTAIN_AIRBAGS("curtain_airbags", FeatureCategory.SAFETY),
    TIRE_PRESSURE_MONITORING("tire_pressure_monitoring", FeatureCategory.SAFETY),
    ISOFIX("isofix", FeatureCategory.SAFETY);

    private final String id;

    @Getter
    private final FeatureCategory category;

    Feature(String id, FeatureCategory category) {
        this.id = id;
        this.category = category;
    }

    @Override
    public String getId() {
        return id;
    }
}
