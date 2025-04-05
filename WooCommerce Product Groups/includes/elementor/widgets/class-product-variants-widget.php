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
        $this->init_hooks();
    }

    private function init_hooks() {
        add_action('init', array($this, 'init_group_taxonomy'));
        add_action('wp_enqueue_scripts', array($this, 'enqueue_assets'));
        add_action('woocommerce_before_single_product', array($this, 'render_variant_selector'));
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

            wp_enqueue_script(
                'wc-product-groups',
                WC_PRODUCT_GROUPS_URL . 'assets/js/product-groups.js',
                array('jquery'),
                WC_PRODUCT_GROUPS_VERSION,
                true
            );
        }
    }

    public function render_variant_selector() {
        global $product;
        
        if (!$product || !is_product()) {
            return;
        }

        $terms = get_the_terms($product->get_id(), 'product_group');
        if (!$terms || is_wp_error($terms)) {
            return;
        }

        $current_group = reset($terms);
        
        // Get all products in this group
        $products_in_group = $this->get_products_in_group($current_group->term_id);
        
        if (count($products_in_group) <= 1) {
            return;
        }

        include WC_PRODUCT_GROUPS_PATH . 'templates/variant-selector.php';
    }

    private function get_products_in_group($group_id) {
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
        $formatted_products = array();

        foreach ($products as $product_post) {
            $product = wc_get_product($product_post->ID);
            if (!$product) continue;

            $formatted_products[] = array(
                'id' => $product->get_id(),
                'title' => $product->get_title(),
                'price' => $product->get_price_html(),
                'image' => $product->get_image('thumbnail'),
                'url' => get_permalink($product->get_id()),
                'stock_status' => $product->get_stock_status(),
                'attributes' => $this->get_product_attributes($product)
            );
        }

        return $formatted_products;
    }

    private function get_product_attributes($product) {
        $attributes = array();
        
        foreach ($product->get_attributes() as $attribute) {
            if ($attribute->is_taxonomy()) {
                $terms = wp_get_post_terms($product->get_id(), $attribute->get_name(), 'all');
                if (!empty($terms) && !is_wp_error($terms)) {
                    $attributes[$attribute->get_name()] = $terms[0]->name;
                }
            } else {
                $values = $attribute->get_options();
                if (!empty($values)) {
                    $attributes[$attribute->get_name()] = reset($values);
                }
            }
        }

        return $attributes;
    }
}