/**
 * 
 */
package za.co.soft;

import java.util.ArrayList;

/**
 * @author mark
 *
 */
public class Site
	{
		String site_name;
		String site_number;
		String region;
		String site_owner;
		String grading;

		String latitude_deg;
		String latitude_min;
		String latitude_sec;

		String longitude_deg;
		String longitude_min;
		String longitude_sec;

		int altitude;

		String physical_address;

		String ground_conditions;
		String road_conditions_type;
		String road_conditions_ease;

		Boolean signboard_attached;

		ArrayList<Container> containerList;

		/**
		 * 
		 */
		public Site()
			{
			}

		/**
		 * @return the site_name
		 */
		public String getSite_name()
			{
				return site_name;
			}

		/**
		 * @param site_name
		 *            the site_name to set
		 */
		public void setSite_name(String site_name)
			{
				this.site_name = site_name;
			}

		/**
		 * @return the site_number
		 */
		public String getSite_number()
			{
				return site_number;
			}

		/**
		 * @param site_number
		 *            the site_number to set
		 */
		public void setSite_number(String site_number)
			{
				this.site_number = site_number;
			}

		/**
		 * @return the region
		 */
		public String getRegion()
			{
				return region;
			}

		/**
		 * @param region
		 *            the region to set
		 */
		public void setRegion(String region)
			{
				this.region = region;
			}

		/**
		 * @return the site_owner
		 */
		public String getSite_owner()
			{
				return site_owner;
			}

		/**
		 * @param site_owner
		 *            the site_owner to set
		 */
		public void setSite_owner(String site_owner)
			{
				this.site_owner = site_owner;
			}

		/**
		 * @return the grading
		 */
		public String getGrading()
			{
				return grading;
			}

		/**
		 * @param grading
		 *            the grading to set
		 */
		public void setGrading(String grading)
			{
				this.grading = grading;
			}

		/**
		 * @return the latitude_deg
		 */
		public String getLatitude_deg()
			{
				return latitude_deg;
			}

		/**
		 * @param latitude_deg
		 *            the latitude_deg to set
		 */
		public void setLatitude_deg(String latitude_deg)
			{
				this.latitude_deg = latitude_deg;
			}

		/**
		 * @return the latitude_min
		 */
		public String getLatitude_min()
			{
				return latitude_min;
			}

		/**
		 * @param latitude_min
		 *            the latitude_min to set
		 */
		public void setLatitude_min(String latitude_min)
			{
				this.latitude_min = latitude_min;
			}

		/**
		 * @return the latitude_sec
		 */
		public String getLatitude_sec()
			{
				return latitude_sec;
			}

		/**
		 * @param latitude_sec
		 *            the latitude_sec to set
		 */
		public void setLatitude_sec(String latitude_sec)
			{
				this.latitude_sec = latitude_sec;
			}

		/**
		 * @return the longitude_deg
		 */
		public String getLongitude_deg()
			{
				return longitude_deg;
			}

		/**
		 * @param longitude_deg
		 *            the longitude_deg to set
		 */
		public void setLongitude_deg(String longitude_deg)
			{
				this.longitude_deg = longitude_deg;
			}

		/**
		 * @return the longitude_min
		 */
		public String getLongitude_min()
			{
				return longitude_min;
			}

		/**
		 * @param longitude_min
		 *            the longitude_min to set
		 */
		public void setLongitude_min(String longitude_min)
			{
				this.longitude_min = longitude_min;
			}

		/**
		 * @return the longitude_sec
		 */
		public String getLongitude_sec()
			{
				return longitude_sec;
			}

		/**
		 * @param longitude_sec
		 *            the longitude_sec to set
		 */
		public void setLongitude_sec(String longitude_sec)
			{
				this.longitude_sec = longitude_sec;
			}

		/**
		 * @return the altitude
		 */
		public int getAltitude()
			{
				return altitude;
			}

		/**
		 * @param altitude
		 *            the altitude to set
		 */
		public void setAltitude(int altitude)
			{
				this.altitude = altitude;
			}

		/**
		 * @return the physical_address
		 */
		public String getPhysical_address()
			{
				return physical_address;
			}

		/**
		 * @param physical_address
		 *            the physical_address to set
		 */
		public void setPhysical_address(String physical_address)
			{
				this.physical_address = physical_address;
			}

		/**
		 * @return the ground_conditions
		 */
		public String getGround_conditions()
			{
				return ground_conditions;
			}

		/**
		 * @param ground_conditions
		 *            the ground_conditions to set
		 */
		public void setGround_conditions(String ground_conditions)
			{
				this.ground_conditions = ground_conditions;
			}

		/**
		 * @return the road_conditions_type
		 */
		public String getRoad_conditions_type()
			{
				return road_conditions_type;
			}

		/**
		 * @param road_conditions_type
		 *            the road_conditions_type to set
		 */
		public void setRoad_conditions_type(String road_conditions_type)
			{
				this.road_conditions_type = road_conditions_type;
			}

		/**
		 * @return the road_conditions_ease
		 */
		public String getRoad_conditions_ease()
			{
				return road_conditions_ease;
			}

		/**
		 * @param road_conditions_ease
		 *            the road_conditions_ease to set
		 */
		public void setRoad_conditions_ease(String road_conditions_ease)
			{
				this.road_conditions_ease = road_conditions_ease;
			}

		/**
		 * @return the signboard_attached
		 */
		public Boolean getSignboard_attached()
			{
				return signboard_attached;
			}

		/**
		 * @param signboard_attached
		 *            the signboard_attached to set
		 */
		public void setSignboard_attached(Boolean signboard_attached)
			{
				this.signboard_attached = signboard_attached;
			}

		/**
		 * @return the containerList
		 */
		public ArrayList<Container> getContainerList()
			{
				return containerList;
			}

		/**
		 * @param containerList
		 *            the containerList to set
		 */
		public void setContainerList(ArrayList<Container> containerList)
			{
				this.containerList = containerList;
			}

	}
