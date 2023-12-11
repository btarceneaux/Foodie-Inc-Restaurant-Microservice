package inc.foodie.repository;

import inc.foodie.bean.S3Documents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S3DocumentRepository extends JpaRepository<S3Documents, Integer>
{
}