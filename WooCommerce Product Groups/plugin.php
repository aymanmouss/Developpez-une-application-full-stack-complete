<?php
/**
 * Plugin Name: WooCommerce Product Groups
 * Description: Creates simple product groups with modern variant selector
 * Version: 1.0.2
 * Author: Your Name
 * Requires at least: 5.0
 * Requires PHP: 7.2
 */

if (!defined('ABSPATH')) {
    exit;
}

// Plugin constants
define('WC_PRODUCT_GROUPS_VERSION', '1.0.2');
define('WC_PRODUCT_GROUPS_FILE', __FILE__);
define('WC_PRODUCT_GROUPS_PATH', plugin_dir_path(__FILE__));
define('WC_PRODUCT_GROUPS_URL', plugin_dir_url(__FILE__));

// Core includes
require_once WC_PRODUCT_GROUPS_PATH . 'includes/class-simple-product-groups.php';
require_once WC_PRODUCT_GROUPS_PATH . 'includes/class-admin.php';

// Initialize plugin
add_action('plugins_loaded', function() {
    if (class_exists('WooCommerce')) {
        Simple_Product_Groups::get_instance();
        new WC_Product_Groups_Admin();
    } else {
        add_action('admin_notices', function() {
            echo '<div class="error"><p>';
            echo 'WooCommerce Product Groups requires WooCommerce to be installed and active.';
            echo '</p></div>';
        });
    }
});