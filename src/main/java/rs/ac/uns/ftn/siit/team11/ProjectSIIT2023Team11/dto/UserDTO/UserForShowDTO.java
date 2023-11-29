package rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.dto.UserDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserForShowDTO {
    private String email;

    private String name;

    private String lastName;

    private String role;

}