jQuery(document).ready(function ($) {
  "use strict";

  $(".variant-selector-header").on("click", function (e) {
    e.preventDefault();
    const container = $(this).closest(".product-variant-selector");
    container.toggleClass("active");

    // Close other dropdowns
    $(".product-variant-selector").not(container).removeClass("active");
  });

  // Close when clicking outside
  $(document).on("click", function (e) {
    if (!$(e.target).closest(".product-variant-selector").length) {
      $(".product-variant-selector").removeClass("active");
    }
  });

  // Prevent dropdown from closing when clicking inside
  $(".variant-list-container").on("click", function (e) {
    e.stopPropagation();
  });
});
