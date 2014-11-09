Google Image Map Tiler
==============

Creates image tiles for Google Maps


Map tile generator for Google Maps

Java based version of implementation at

http://blog.mikecouturier.com/2011/07/create-zoomable-images-using-google.html
by Mike Couturier

To use run after compiling

```
java TilesGenerator [1-15] [filename] 

```
where [1-15] signifies the zoom level and the [filename] is the image you want to segment.

Then use in your webpage

```
<!DOCTYPE html>
<html>
<head>
    <title>Image map types</title>
    <style>
        html, body, #map-canvas {
        height: 100%;
        width: 100%;
        margin: 0px;
        padding: 0px
        }
    </style>
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp"></script>
    <script>
var moonTypeOptions = {
  getTileUrl: function(coord, zoom) {
      var normalizedCoord = getNormalizedCoord(coord, zoom);
      if (!normalizedCoord) {
        return null;
      }
      var bound = Math.pow(2, zoom);
       return 'file:///[filename]' +
          '/tile_' + zoom + '_' + normalizedCoord.x + '_' +
          (normalizedCoord.y    ) + '.png';
  },
  tileSize: new google.maps.Size(256, 256),
  maxZoom: 3,
  minZoom: 0,
  radius: 1738000,
  name: 'example'
};

var moonMapType = new google.maps.ImageMapType(moonTypeOptions);

function initialize() {
  var myLatlng = new google.maps.LatLng(0, 0);
  var mapOptions = {
    center: myLatlng,
    zoom: 1,
    streetViewControl: false,
    mapTypeControlOptions: {
      mapTypeIds: ['example']
    }
  };

  var map = new google.maps.Map(document.getElementById('map-canvas'),
      mapOptions);
  map.mapTypes.set('example', moonMapType);
  map.setMapTypeId('example');
}

// Normalizes the coords that tiles repeat across the x axis (horizontally)
// like the standard Google map tiles.
function getNormalizedCoord(coord, zoom) {
  var y = coord.y;
  var x = coord.x;

  // tile range in one direction range is dependent on zoom level
  // 0 = 1 tile, 1 = 2 tiles, 2 = 4 tiles, 3 = 8 tiles, etc
  var tileRange = 1 << zoom;

  // don't repeat across y-axis (vertically)
  if (y < 0 || y >= tileRange) {
    return null;
  }

  // don't repeat across x-axis
  if (x < 0 || x >= tileRange) {
    //x = (x % tileRange + tileRange) % tileRange;
    return null;
  }

  return {
    x: x,
    y: y
  };
}

google.maps.event.addDomListener(window, 'load', initialize);

</script>
</head>
<body>
<div id="map-canvas"></div>
</body>
</html>

```

