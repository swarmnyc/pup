using System.Security.Claims;
using System.Threading.Tasks;
using AspNet.Identity.MongoDB;
using Microsoft.AspNet.Identity;

namespace SWARM.PuP.Web.Models
{
    // You can add profile data for the user by adding more properties to your PuPUser class, please visit http://go.microsoft.com/fwlink/?LinkID=317594 to learn more.
    public class PuPUser : IdentityUser
    {
        public string ChatId { get; set; }
        public string DisplayName { get; set; }
        public string PictureUrl { get; set; }
    }
}