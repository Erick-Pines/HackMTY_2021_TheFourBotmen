//test data
/*arr=[
	{
		id: "612af",
		lon: 0.0,
		lat: 0.0
	},
	{
		id: "612af1",
		lon: 0.0,
		lat: 0.0
	},
	{
		id: "6233478",
		lon: 1.0,
		lat: 0.0
	}
]*/
function distance(lat,lon,lat2,lon2){
	return Math.sqrt(Math.pow(lon2-lon,2)+Math.pow(lat2-lat,2));
}
function getCollisionList(data){
	collisions=[];
	for(let i=0;i<data.length;i++){
		let current=data[i];
		for(let j=0;j<data.length;j++){
			if(j!=i){
				let other=data[j]; 
				// if distance is shorter than aproximately 2.5 m
				if(distance(current.lat,current.lon,
					other.lat, other.lon) <= 0.0000308642)
					collisions.push({
						from: current.user_id,
						with: other.user_id});
			}
		}
	}
	return collisions;
}

console.log(arr);
console.log(getCollisionList(arr));



