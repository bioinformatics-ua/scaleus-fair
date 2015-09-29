/**
 * @lgonzalez
 */

var services = angular.module('restapp.services', []);


services.factory('SharedService', ['$rootScope', function ($rootScope) {
        return {
            selectedDataset: 'default',
            update: function (item) {
                this.selectedDataset = item;
                $rootScope.$broadcast('datasetChanged', item);
            }
        };
    }]);

services.factory('RDFDataService', function ($resource) {
    return $resource('../api/v1/data/:id', {id: '@id'}, {
        get: {transformResponse: function (data, headersGetter, status) {
                return {content: data, status: status};
            }},
        save: {method: 'POST', headers: {'Content-Type': 'application/x-www-form-urlencoded'}}
    });
});

services.factory('DatasetsService', function ($resource) {
    return $resource('../api/v1/dataset/:id', {id: '@id'}, {
        query: {method: 'GET', isArray: true},
        save: {method: 'POST'},
        delete: {method: 'DELETE'}
    });

});

services.factory('DatasetsService', function($resource) {
	return $resource('../api/v1/dataset/:id', {id: '@id'}, {
		query: {method:'GET', isArray:true},
		save: {method:'POST'},
		delete: {method:'DELETE'}
	});
});

services.factory('NamespacesService', function($resource) {
	return $resource('../api/v1/namespaces/:dataset/:prefix', {}, {
		get: {method:'GET'},
		save: {method:'POST'},
		delete: {method:'DELETE'}
	});   
});

services.factory('TriplesService', function($resource) {
	return function(deleteBody){
		return $resource('../api/v1/store/:dataset', {dataset: '@dataset'}, {
			save: {method:'POST'},
			delete: {method:'DELETE',
				headers: {"Content-Type": "application/json;charset=utf-8"},
				data: deleteBody}
		});   
	};
});

services.factory('QueriesService', function($resource) {
	return $resource('../api/v1/sparqler/:dataset/sparql', {}, {
		query: {method:'GET'}	
	});   
});

services.factory('ResourceService', function($resource) {
	return $resource('../api/v1/resource/:dataset/:prefix/:resource/js', {}, {
		get: {method:'GET'}	// TODO - check this
	});   
});



//services.factory('APIservice', function($http) {
//
//	var scaleusAPI = {};
//
//	// GET describe resources
//	scaleusAPI.getResources = function (database, prefix, resource) {
//		return $http.get("../api/v1/resource/"+database+"/"+prefix+"/"+resource+"/js");
//	}
//
//	return scaleusAPI;
//});
