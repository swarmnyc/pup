namespace SWARM.PuP.Web.ViewModels
{
    public class RequestResult<T>
    {
        public T Data { get; set; }

        public bool Success { get; set; }

        public string ErrorMessage { get; set; }
    }
}