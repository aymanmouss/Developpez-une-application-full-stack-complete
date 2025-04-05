jQuery(document).ready(function ($) {
  "use strict";

  var VariantDropdown = {
    init: function () {
      this.bindEvents();
      this.initDropdowns();
    },

    bindEvents: function () {
      // Unbind existing events first to prevent duplicates
      $(document).off("click.variantDropdown");

      // Bind click event to dropdown header
      $(document).on(
        "click.variantDropdown",
        ".variant-dropdown-header",
        function (e) {
          e.preventDefault();
          e.stopPropagation();
          console.log("Dropdown clicked"); // Debug log

          var $container = $(this).closest(".variant-dropdown-container");

          // Close other dropdowns
          $(".variant-dropdown-container")
            .not($container)
            .removeClass("active");

          // Toggle current dropdown
          $container.toggleClass("active");
        }
      );

      // Close dropdown when clicking outside
      $(document).on("click.variantDropdownOutside", function (e) {
        if (!$(e.target).closest(".variant-dropdown-container").length) {
          $(".variant-dropdown-container").removeClass("active");
        }
      });

      // Prevent dropdown from closing when clicking inside
      $(document).on(
        "click.variantDropdownContent",
        ".variant-dropdown-content",
        function (e) {
          e.stopPropagation();
        }
      );
    },

    initDropdowns: function () {
      // Initialize all dropdowns in their closed state
      $(".variant-dropdown-container").removeClass("active");
    },
  };

  // Initialize on document ready
  VariantDropdown.init();

  // Initialize when Elementor frontend is ready
  if (typeof elementorFrontend !== "undefined") {
    elementorFrontend.hooks.addAction(
      "frontend/element_ready/product_variants.default",
      function () {
        VariantDropdown.init();
      }
    );
  }

  // Make it available globally
  window.VariantDropdown = VariantDropdown;
});
