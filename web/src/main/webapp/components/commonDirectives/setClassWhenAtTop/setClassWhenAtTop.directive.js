angular.module('rehabstodApp').directive('setClassWhenAtTop', function ($window) {
    'use strict';
    var $win = angular.element($window); // wrap window object as jQuery object

    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var topClass = attrs.setClassWhenAtTop, // get CSS class from directive's attribute value
                parent = element.parent(),
                useHide = attrs.useHide === 'true';

            var paddingTop = 0;
            if (attrs.paddingTop) {
                paddingTop = parseInt(attrs.paddingTop, 10);
            }

            var onScroll = function () {
                var elementHeight = element.outerHeight();
                var offsetTop = parent.offset().top - paddingTop;

                if ($win.scrollTop() >= offsetTop) {
                    if (!useHide) {
                        parent.height(elementHeight);
                    }

                    element.css('top', paddingTop);
                    element.addClass(topClass);
                } else {
                    element.removeClass(topClass);
                    if (!useHide) {
                        parent.css('height', 'auto');
                    }
                }
            };

            $win.on('scroll', onScroll);

            scope.$on('$destroy', function() {
                $win.unbind('scroll', onScroll);
            });
        }
    };
});