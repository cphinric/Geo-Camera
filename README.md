The Geo-Camera application will require the use of the Maps API
and the camera sensor. There are two primary work flows: 1) The user will use the Camera
sensor (either through an implicit call to the default camera application, or through the
Camera API) to take a photograph. 2) The user can view a map centered on the user’s
current location, populated with markers showing locations that the user has taken photos.
These markers should be clickable to show the photo or photos taken at that location, as
well as a text description of the photo. This workflow may be two fragments in the same
activity, or two separate activities.
