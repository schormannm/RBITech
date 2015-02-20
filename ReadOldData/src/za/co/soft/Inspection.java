/**
 * 
 */
package za.co.soft;

/**
 * @author mark
 *
 */
public class Inspection
	{

		String inspection_date;

		String inspector;
		String project_specialist;
		String job_number;
		String report_number;
		String mpi_report_number;
		String fall_arrest_report_number;
		String dld_report_number;
		String visually_checked;

		Site site;

		/**
		 * 
		 */
		public Inspection()
			{
				site = new Site();
			}

		/**
		 * @param inspection_date
		 * @param inspector
		 * @param project_specialist
		 * @param job_number
		 * @param report_number
		 * @param mpi_report_number
		 * @param fall_arrest_report_number
		 * @param dld_report_number
		 * @param visually_checked
		 * @param site
		 */
		public Inspection(String inspection_date, String inspector, String project_specialist,
				String job_number, String report_number, String mpi_report_number,
				String fall_arrest_report_number, String dld_report_number, String visually_checked, Site site)
			{
				super();
				this.inspection_date = inspection_date;
				this.inspector = inspector;
				this.project_specialist = project_specialist;
				this.job_number = job_number;
				this.report_number = report_number;
				this.mpi_report_number = mpi_report_number;
				this.fall_arrest_report_number = fall_arrest_report_number;
				this.dld_report_number = dld_report_number;
				this.visually_checked = visually_checked;
				this.site = site;
			}

	}
