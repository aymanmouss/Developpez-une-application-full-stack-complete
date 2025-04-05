<?php
if (!defined('ABSPATH')) {
    exit;
}

class Simple_Product_Groups {
    protected static $instance = null;

    public static function get_instance() {
        if (null === self::$instance) {
            self::$instance = new self();
        }
        return self::$instance;
    }

    public function __construct() {
        if ($this->check_dependencies()) {
            $this->init_hooks();
        }
    }

    private function check_dependencies() {
        $all_good = true;
        
        if (!class_exists('WooCommerce')) {
            add_action('admin_notices', function() {
                echo '<div class="error"><p>';
                echo esc_html__('WooCommerce Product Groups requires WooCommerce to be installed and active.', 'wc-product-groups');
                echo '</p></div>';
            });
            $all_good = false;
        }

        if (!did_action('elementor/loaded')) {
            add_action('admin_notices', function() {
                echo '<div class="error"><p>';
                echo esc_html__('WooCommerce Product Groups requires Elementor to be installed and active.', 'wc-product-groups');
                echo '</p></div>';
            });
            $all_good = false;
        }

        return $all_good;
    }

    private function init_hooks() {
        add_action('init', array($this, 'init_group_taxonomy'));
        add_action('wp_enqueue_scripts', array($this, 'enqueue_assets'));
        add_action('elementor/widgets/register', array($this, 'register_elementor_widget'));
    }

    public function init_group_taxonomy() {
        $args = array(
            'hierarchical' => true,
            'public' => true,
            'show_ui' => true,
            'show_admin_column' => true,
            'show_in_menu' => true,
            'show_in_nav_menus' => true,
            'show_in_rest' => true,
            'labels' => array(
                'name' => __('Product Groups', 'wc-product-groups'),
                'singular_name' => __('Product Group', 'wc-product-groups'),
                'menu_name' => __('Product Groups', 'wc-product-groups'),
                'all_items' => __('All Groups', 'wc-product-groups'),
                'edit_item' => __('Edit Group', 'wc-product-groups')
            )
        );
        
        register_taxonomy('product_group', 'product', $args);
    }

    public function enqueue_assets() {
        if (is_product()) {
            wp_enqueue_style(
                'wc-product-groups',
                WC_PRODUCT_GROUPS_URL . 'assets/css/product-groups.css',
                array(),
                WC_PRODUCT_GROUPS_VERSION
            );

            wp_enqueue_style(
                'wc-product-groups-elementor',
                WC_PRODUCT_GROUPS_URL . 'assets/css/elementor-widget.css',
                array(),
                WC_PRODUCT_GROUPS_VERSION
            );

            wp_enqueue_script(
                'wc-product-groups',
                WC_PRODUCT_GROUPS_URL . 'assets/js/product-groups.js',
                array('jquery'),
                WC_PRODUCT_GROUPS_VERSION,
                true
            );

            wp_enqueue_script(
                'wc-product-groups-elementor',
                WC_PRODUCT_GROUPS_URL . 'assets/js/elementor-widget.js',
                array('jquery'),
                WC_PRODUCT_GROUPS_VERSION,
                true
            );
        }
    }

    public function register_elementor_widget($widgets_manager) {
        require_once WC_PRODUCT_GROUPS_PATH . 'includes/elementor/widgets/class-product-variants-widget.php';
        $widgets_manager->register(new Product_Variants_Widget());
    }
}