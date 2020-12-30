# Dijkstra_Map_Pathfinder

A simple pathfinding application using Dijkstra's algorithm wtih adjascency graph and without min-heap, thus a time complexity of O(|V|^2). It is originally my Freshman year's partnered project (which I did >95% of the work) but added a few more features afterwards for extra credit.

### The code takes in specific arguments in the form of:
`<map_data_text_file_path>` `<`arguments: `--show`, `--directions`, or both`>` `<start_intersection_name>` `<destination_intersection_name>`

Here,
* `--show` displays the map in a GUI
* `-directions` will calculate the shortest path
* `start_intersection_name` and `destination_intersection_name` must be included if and only if `--destination` argument is used.

Examples:
`src/monroe.txt --show --directions i185852 i294475`
`src/ur.txt --show`
`src/ur.txt --directions ITS HYLAN`

### Map data text file must be in the format of:
`i  <name>  <latitude> <longitude>`
OR
`r  <name>  <from_intersection_name>  <to_intersection_name>`
(Each argumument must be separated with indentation)

Here,
* `i` denotes an intersection (vertex), and `r` denotes a road (edge).
* `name` can be any `String` text supported by Java.
* `latitude` and `longitude` accepts decimals (`double` type).

Examples:
`i	i6	43.130186	-77.631336`
`r	RAIL55	STATION003	STATION004`
