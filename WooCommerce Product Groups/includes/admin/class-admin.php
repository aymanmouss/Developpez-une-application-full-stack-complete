<?php
if (!defined('ABSPATH')) {
    exit;
}

class WC_Product_Groups_Admin {
    public function __construct() {
        add_filter('manage_product_posts_columns', array($this, 'add_group_column'));
        add_action('manage_product_posts_custom_column', array($this, 'show_group_column'), 10, 2);
        add_action('admin_enqueue_scripts', array($this, 'enqueue_admin_assets'));
    }

    public function enqueue_admin_assets() {
        $screen = get_current_screen();
        if ($screen && 'product' === $screen->post_type) {
            wp_enqueue_style(
                'wc-product-groups-admin',
                WC_PRODUCT_GROUPS_URL . 'assets/css/admin.css',
                array(),
                WC_PRODUCT_GROUPS_VERSION
            );
        }
    }

    public function add_group_column($columns) {
        $new_columns = array();
        foreach ($columns as $key => $value) {
            $new_columns[$key] = $value;
            if ($key === 'name') {
                $new_columns['product_group'] = __('Product Group', 'wc-product-groups');
            }
        }
        return $new_columns;
    }

    public function show_group_column($column, $post_id) {
        if ($column === 'product_group') {
            $terms = get_the_terms($post_id, 'product_group');
            if ($terms && !is_wp_error($terms)) {
                $group_names = array();
                foreach ($terms as $term) {
                    $group_names[] = sprintf(
                        '<a href="%s">%s</a>',
                        esc_url(admin_url('edit.php?post_type=product&product_group=' . $term->slug)),
                        esc_html($term->name)
                    );
                }
                echo implode(', ', $group_names);
            }
        }
    }
}