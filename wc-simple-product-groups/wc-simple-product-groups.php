<?php
/**
 * Plugin Name: Simple WooCommerce Product Groups Dropdown
 * Description: Creates simple product groups with dropdown selector
 * Version: 1.0.0
 * Author: Your Name
 * Requires at least: 5.0
 * Requires PHP: 7.2
 */

if (!defined('ABSPATH')) {
    exit;
}

class WC_Simple_Product_Groups {
    private static $instance = null;
    
    public static function instance() {
        if (null === self::$instance) {
            self::$instance = new self();
        }
        return self::$instance;
    }

    public function __construct() {
        // Initialize plugin
        add_action('plugins_loaded', array($this, 'init'));
    }

    public function init() {
        // Check if WooCommerce is active
        if (!class_exists('WooCommerce')) {
            add_action('admin_notices', array($this, 'woocommerce_missing_notice'));
            return;
        }

        // Register taxonomy
        add_action('init', array($this, 'register_taxonomy'));
        
        // Add variant selector to product page
        add_action('woocommerce_before_add_to_cart_form', array($this, 'display_variant_selector'));
        
        // Enqueue scripts and styles
        add_action('wp_enqueue_scripts', array($this, 'enqueue_assets'));
    }

    public function woocommerce_missing_notice() {
        echo '<div class="error"><p>Simple Product Groups requires WooCommerce to be installed and active.</p></div>';
    }

    public function register_taxonomy() {
        register_taxonomy('product_group', 'product', array(
            'hierarchical' => true,
            'public' => true,
            'show_ui' => true,
            'show_admin_column' => true,
            'show_in_nav_menus' => true,
            'show_in_rest' => true,
            'labels' => array(
                'name' => 'Product Groups',
                'singular_name' => 'Product Group',
                'menu_name' => 'Product Groups',
                'all_items' => 'All Groups',
                'edit_item' => 'Edit Group'
            )
        ));
    }

    public function enqueue_assets() {
        if (is_product()) {
            wp_enqueue_style(
                'wc-product-groups',
                plugin_dir_url(__FILE__) . 'assets/css/style.css',
                array(),
                '1.0.0'
            );

            wp_enqueue_script(
                'wc-product-groups',
                plugin_dir_url(__FILE__) . 'assets/js/script.js',
                array('jquery'),
                '1.0.0',
                true
            );
        }
    }

    public function display_variant_selector() {
        global $product;
        
        if (!$product) return;

        // Get product groups
        $terms = get_the_terms($product->get_id(), 'product_group');
        if (!$terms || is_wp_error($terms)) return;

        $current_group = reset($terms);
        $variants = $this->get_group_variants($current_group->term_id);
        
        if (count($variants) <= 1) return;

        // Get current product attributes
        $current_attributes = $this->get_product_attributes($product);
        
        include plugin_dir_path(__FILE__) . 'templates/variant-dropdown.php';
    }

    private function get_group_variants($group_id) {
        $args = array(
            'post_type' => 'product',
            'posts_per_page' => -1,
            'tax_query' => array(
                array(
                    'taxonomy' => 'product_group',
                    'field' => 'term_id',
                    'terms' => $group_id
                )
            ),
            'post_status' => 'publish'
        );

        $products = get_posts($args);
        $variants = array();

        foreach ($products as $product_post) {
            $variant = wc_get_product($product_post->ID);
            if (!$variant) continue;

            $variants[] = array(
                'id' => $variant->get_id(),
                'title' => $variant->get_title(),
                'price' => $variant->get_price_html(),
                'url' => get_permalink($variant->get_id()),
                'stock_status' => $variant->get_stock_status(),
                'attributes' => $this->get_product_attributes($variant)
            );
        }

        return $variants;
    }

    private function get_product_attributes($product) {
        $attributes = array();
        foreach ($product->get_attributes() as $attribute) {
            if ($attribute->is_taxonomy()) {
                $terms = wp_get_post_terms($product->get_id(), $attribute->get_name());
                if (!empty($terms) && !is_wp_error($terms)) {
                    $term = reset($terms);
                    $attributes[$attribute->get_name()] = $term->name;
                }
            }
        }
        return $attributes;
    }
}

// Initialize plugin
function wc_simple_product_groups() {
    return WC_Simple_Product_Groups::instance();
}

wc_simple_product_groups();